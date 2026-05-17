# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Contract-test fixture values for (operation, parameter) pairs.

Goal
----

Replace the previous hand-maintained ``FIXTURES`` table (~50 entries) with a
schema-driven derivation that consults the generated Rust client:

* operation signatures already known to ``gen_client_tests.OPERATIONS``;
* model required-field lists and field types parsed into
  ``gen_client_tests._MODEL_REQUIRED`` / ``_MODEL_FIELDS``.

Every required parameter therefore has a *type-level default* — strings become
``""``, integers become ``0``, models recurse into their own required fields,
and so on.  The WireMock mappings emitted by ``gen_wiremock_mappings.py`` use
``[^/]+`` segment wildcards for path params and do not validate request
bodies, so concrete values do not need to be human-meaningful: they only need
to type-check the generated client constructors.

For the very small set of cases where the OpenAPI spec carries a constraint
the Rust client cannot express (notably ``minItems``), ``OVERRIDES`` captures
the exact override needed.  Anything not in ``OVERRIDES`` is derived
automatically.

Step 1 of the fixture refactor only introduces this module and rewires
``gen_client_tests.py`` to consume it; the (currently identical) defaulting
helpers in ``gen_wiremock_mappings.py`` stay untouched to keep the diff
surgical.  Step 2 will consolidate them once a concrete need for shared
path-param IDs appears (e.g. tightening WireMock from ``[^/]+`` to exact
path matching).
"""

from __future__ import annotations

import re
from typing import Any


# ---------------------------------------------------------------------------
# ModelValue — abstract literal for a generated model constructor call.
# ---------------------------------------------------------------------------
#
# Re-exported here (rather than living only in gen_client_tests.py) so that
# OVERRIDES below can reference it without creating a circular import; the
# canonical alias remains ``gen_client_tests.ModelValue``.
# ---------------------------------------------------------------------------


class ModelValue:
    """A nested model literal, e.g. ``QueryTableRequest(k=1, vector=...)``.

    ``kwargs`` maps generated-model field names (snake_case) to abstract
    values (scalars / lists / dicts / ``ModelValue``).  The per-language
    renderers in ``gen_client_tests.py`` translate this into the appropriate
    constructor / builder expression.
    """

    __slots__ = ("class_name", "kwargs")

    def __init__(self, class_name: str, **kwargs: object) -> None:
        self.class_name = class_name
        self.kwargs = kwargs


# ---------------------------------------------------------------------------
# Schema-aware defaults driven by the Rust client metadata.
# ---------------------------------------------------------------------------
#
# These helpers take the two maps populated by ``gen_client_tests.py``:
#
#   * ``model_required[cls]``  → ordered list of required field names
#                                 (mirrors the model's ``pub fn new`` args).
#   * ``model_fields[cls][f]`` → ``(inner_rust_type_string, is_option)`` for
#                                 every struct field of ``cls``.
#
# Field types are kept as raw Rust strings (e.g. ``"i64"``, ``"String"``,
# ``"Vec<models::Foo>"``) so this module need not duplicate the Rust-type
# classifier already present in ``gen_client_tests.py``.  The mapping rules
# below cover every shape currently produced by the OpenAPI generator for
# this spec; an unknown shape raises so a contributor sees exactly which
# Rust type lacks a default.
# ---------------------------------------------------------------------------


# Rust scalar / wrapper tokens → their literal-friendly Python defaults.
# Anything compound (``Vec<...>`` / ``models::Cls`` / ``HashMap<...>``) is
# handled explicitly in ``_default_for_rust_type`` below.
_SCALAR_RUST_DEFAULTS: dict[str, Any] = {
    # NB: strings default to "x" — never "" — because path-parameter slots
    # are substituted directly into URL templates and an empty string
    # collapses the segment, breaking the WireMock ``[^/]+`` matcher
    # (which requires *at least one* non-slash character).  Empty strings
    # in *body* slots are accepted by the OpenAPI generator and would be
    # fine in isolation, but the resolver has no per-slot context so we
    # use the conservative non-empty placeholder uniformly.
    "&str": "x",
    "String": "x",
    "bool": False,
    "i32": 0,
    "i64": 0,
    "u32": 0,
    "u64": 0,
    "f32": 0.0,
    "f64": 0.0,
}


def _strip_option(rust_type: str) -> tuple[str, bool]:
    """Return ``(inner_type, is_option)``.  Idempotent for non-Option inputs."""
    t = rust_type.strip()
    if t.startswith("Option<") and t.endswith(">"):
        return t[len("Option<") : -1].strip(), True
    return t, False


def _vec_element(rust_type: str) -> str | None:
    """Return ``T`` for ``Vec<T>``, else None."""
    t = rust_type.strip()
    if t.startswith("Vec<") and t.endswith(">"):
        return t[4:-1].strip()
    return None


def _model_class(rust_type: str) -> str | None:
    """Return ``Cls`` for ``models::Cls`` / ``Box<models::Cls>``, else None.

    The OpenAPI generator wraps recursive / large models in ``Box`` on the
    Rust side; we treat both ``models::X`` and ``Box<models::X>`` as
    references to model class ``X``.
    """
    t = rust_type.strip()
    m = re.match(r"^Box<\s*models::([A-Z][A-Za-z0-9]*)\s*>$", t)
    if m:
        return m.group(1)
    m = re.match(r"^models::([A-Z][A-Za-z0-9]*)$", t)
    if m:
        return m.group(1)
    return None


def _default_for_rust_type(
    rust_type: str,
    model_required: dict[str, list[str]],
    model_fields: dict[str, dict[str, tuple[str, bool]]],
    visiting: set[str],
) -> Any:
    """Return a type-appropriate default value for an arbitrary Rust type.

    ``visiting`` guards against cycles in self-referential models — a recursed
    class returns an empty ``ModelValue`` so the outer caller still produces
    a well-formed constructor expression.
    """
    inner, _is_opt = _strip_option(rust_type)

    if inner in _SCALAR_RUST_DEFAULTS:
        return _SCALAR_RUST_DEFAULTS[inner]

    # Bytes body — generated client always uses ``Vec<u8>``.
    if inner == "Vec<u8>":
        return b""

    # Map<String,String> — both qualified and bare forms occur.
    if re.match(
        r"^(std::collections::)?HashMap<\s*String\s*,\s*String\s*>$", inner
    ):
        return {}

    # Generic Vec<T> — empty list satisfies any non-minItems constraint.
    elem = _vec_element(inner)
    if elem is not None:
        return []

    cls = _model_class(inner)
    if cls is not None:
        return _default_for_model(cls, model_required, model_fields, visiting)

    raise SystemExit(
        f"ERROR: cannot derive contract-test default for Rust type "
        f"'{rust_type}'.  Extend ci/cts/fixtures.py._default_for_rust_type()."
    )


def _default_for_model(
    cls: str,
    model_required: dict[str, list[str]],
    model_fields: dict[str, dict[str, tuple[str, bool]]],
    visiting: set[str],
) -> ModelValue:
    """Construct an empty / minimal ``ModelValue`` for class ``cls``.

    Populates *every* required field with a recursive default value so the
    generated ``models::Cls::new(...)`` call always type-checks.  Cyclic
    self-reference returns an empty ``ModelValue(cls)`` — the cycle then
    breaks at the next required-field round because every cycle in the
    Lance schema closes through at least one optional edge in practice.
    """
    if cls in visiting:
        return ModelValue(cls)
    if cls not in model_required:
        # Unknown class — surface as an explicit error rather than silently
        # producing ``ModelValue(cls)`` and letting the renderer guess.
        raise SystemExit(
            f"ERROR: model class '{cls}' not present in generated Rust "
            "client.  Either regenerate (`make gen-rust`) or check the "
            "fixture references."
        )

    next_visiting = visiting | {cls}
    fields = model_fields.get(cls, {})
    kwargs: dict[str, Any] = {}
    for fname in model_required[cls]:
        ftype, _is_opt = fields.get(fname, ("", False))
        if not ftype:
            raise SystemExit(
                f"ERROR: model {cls}.{fname} is required but its Rust type "
                "is not recorded in _MODEL_FIELDS."
            )
        kwargs[fname] = _default_for_rust_type(
            ftype, model_required, model_fields, next_visiting
        )
    return ModelValue(cls, **kwargs)


# Public — called from gen_client_tests._fixture_for.
def default_value_for_param(
    abs_kind: str,
    abs_model: str,
    model_required: dict[str, list[str]],
    model_fields: dict[str, dict[str, tuple[str, bool]]],
) -> Any:
    """Return a default abstract value for a Rust-signature parameter.

    ``abs_kind`` is one of the ``AbstractType.*`` constants defined in
    ``gen_client_tests.py`` (``"string"``, ``"i32"``, ..., ``"model"``).
    For ``MODEL``, ``abs_model`` carries the class name.

    The function is intentionally typed against plain strings rather than
    importing ``AbstractType`` from ``gen_client_tests`` to keep this module
    free of a circular dependency.
    """
    # Note: keep this table in sync with ``AbstractType`` constants.  We
    # accept the constant *values* (e.g. ``"string"``) so a mismatch is
    # detected at fixture-resolution time rather than silently substituting
    # the wrong default.
    if abs_kind == "string":
        # See _SCALAR_RUST_DEFAULTS — non-empty placeholder is mandatory
        # for path parameters to satisfy WireMock's ``[^/]+`` matcher.
        return "x"
    if abs_kind == "i32" or abs_kind == "i64":
        return 0
    if abs_kind == "f32":
        return 0.0
    if abs_kind == "bool":
        return False
    if abs_kind == "bytes":
        return b""
    if abs_kind == "str_map":
        return {}
    if abs_kind == "model":
        return _default_for_model(abs_model, model_required, model_fields, set())
    raise SystemExit(
        f"ERROR: cannot derive contract-test default for AbstractType kind "
        f"'{abs_kind}'.  Extend ci/cts/fixtures.py.default_value_for_param()."
    )


# ---------------------------------------------------------------------------
# OVERRIDES — explicit (operation, param) values the auto-derivation cannot
# infer from the Rust client alone.
# ---------------------------------------------------------------------------
#
# Each entry is keyed by ``operation`` (PascalCase, e.g. ``"AlterTransaction"``)
# and ``parameter`` (snake_case, matches the Rust ``pub async fn`` arg name).
# Add an entry only when the auto-derived default is *known* to violate a
# spec constraint that is not encoded in the Rust signature — typically:
#
#   * OpenAPI ``minItems`` / ``minLength`` constraints (Rust client does
#     not express them at the type level);
#   * String enums whose member names matter to a downstream WireMock
#     matcher (none today — WireMock currently uses wildcard path matching);
#   * Path / query parameters whose value must align with a WireMock
#     stub that filters on exact value (none today).
#
# Keep this table as small as possible.  Each row should carry a one-line
# comment explaining *why* the default is wrong, so future readers can
# delete the row when the underlying constraint is lifted or recorded in
# the Rust client.
# ---------------------------------------------------------------------------


OVERRIDES: dict[str, dict[str, object]] = {
    # Spec declares ``actions: minItems=1`` on AlterTransactionRequest; the
    # Rust client surfaces ``actions: Vec<...>`` with no length floor, so
    # the auto-derived default ``vec![]`` would fail server-side schema
    # validation.  Supply a single all-optional action so the request
    # validates without committing to any specific action variant.
    "AlterTransaction": {
        "alter_transaction_request": ModelValue(
            "AlterTransactionRequest",
            actions=[ModelValue("AlterTransactionAction")],
        ),
    },
}
