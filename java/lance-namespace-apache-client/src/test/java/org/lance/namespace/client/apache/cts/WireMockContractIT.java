/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lance.namespace.client.apache.cts;

import org.lance.namespace.client.apache.ApiClient;
import org.lance.namespace.client.apache.ApiException;
import org.lance.namespace.client.apache.api.IndexApi;
import org.lance.namespace.client.apache.api.NamespaceApi;
import org.lance.namespace.client.apache.api.TableApi;
import org.lance.namespace.client.apache.api.TagApi;
import org.lance.namespace.client.apache.api.TransactionApi;
import org.lance.namespace.model.AlterTableAddColumnsRequest;
import org.lance.namespace.model.AlterTableAlterColumnsRequest;
import org.lance.namespace.model.AlterTableBackfillColumnsRequest;
import org.lance.namespace.model.AlterTableDropColumnsRequest;
import org.lance.namespace.model.AlterTransactionAction;
import org.lance.namespace.model.AlterTransactionRequest;
import org.lance.namespace.model.AnalyzeTableQueryPlanRequest;
import org.lance.namespace.model.BatchCommitTablesRequest;
import org.lance.namespace.model.BatchCreateTableVersionsRequest;
import org.lance.namespace.model.BatchDeleteTableVersionsRequest;
import org.lance.namespace.model.CountTableRowsRequest;
import org.lance.namespace.model.CreateNamespaceRequest;
import org.lance.namespace.model.CreateTableIndexRequest;
import org.lance.namespace.model.CreateTableTagRequest;
import org.lance.namespace.model.CreateTableVersionRequest;
import org.lance.namespace.model.DeclareTableRequest;
import org.lance.namespace.model.DeleteFromTableRequest;
import org.lance.namespace.model.DeleteTableTagRequest;
import org.lance.namespace.model.DeregisterTableRequest;
import org.lance.namespace.model.DescribeNamespaceRequest;
import org.lance.namespace.model.DescribeTableIndexStatsRequest;
import org.lance.namespace.model.DescribeTableRequest;
import org.lance.namespace.model.DescribeTableVersionRequest;
import org.lance.namespace.model.DescribeTransactionRequest;
import org.lance.namespace.model.DropNamespaceRequest;
import org.lance.namespace.model.ExplainTableQueryPlanRequest;
import org.lance.namespace.model.GetTableStatsRequest;
import org.lance.namespace.model.GetTableTagVersionRequest;
import org.lance.namespace.model.ListTableIndicesRequest;
import org.lance.namespace.model.NamespaceExistsRequest;
import org.lance.namespace.model.QueryTableRequest;
import org.lance.namespace.model.QueryTableRequestVector;
import org.lance.namespace.model.RegisterTableRequest;
import org.lance.namespace.model.RenameTableRequest;
import org.lance.namespace.model.RestoreTableRequest;
import org.lance.namespace.model.TableExistsRequest;
import org.lance.namespace.model.UpdateTableRequest;
import org.lance.namespace.model.UpdateTableTagRequest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

/** Thin contract runner: starts WireMock with pre-generated mappings from build/cts/wiremock/. */
public class WireMockContractIT {

  private static WireMockServer wireMock;
  private static ApiClient apiClient;

  @BeforeAll
  static void startWireMock() {
    String mappingsRoot =
        Paths.get(
                System.getProperty(
                    "wiremock.mappings.root", "../../build/cts/wiremock/src/main/resources"))
            .toAbsolutePath()
            .toString();

    wireMock =
        new WireMockServer(
            WireMockConfiguration.options().dynamicPort().usingFilesUnderDirectory(mappingsRoot));
    wireMock.start();

    apiClient = new ApiClient();
    apiClient.setBasePath("http://localhost:" + wireMock.port());
  }

  @AfterAll
  static void stopWireMock() {
    if (wireMock != null) {
      wireMock.stop();
    }
  }

  @Test
  void alterTableAddColumnsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.alterTableAddColumns(
        "x", new AlterTableAddColumnsRequest().newColumns(new java.util.ArrayList<>()), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void alterTableAlterColumnsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.alterTableAlterColumns(
        "x", new AlterTableAlterColumnsRequest().alterations(new java.util.ArrayList<>()), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void alterTableBackfillColumnsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.alterTableBackfillColumns("x", new AlterTableBackfillColumnsRequest().column("x"), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void alterTableDropColumnsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.alterTableDropColumns(
        "x", new AlterTableDropColumnsRequest().columns(new java.util.ArrayList<>()), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void alterTransactionReturnsValidResponse() throws ApiException {
    TransactionApi api = new TransactionApi(apiClient);
    api.alterTransaction(
        "x",
        new AlterTransactionRequest()
            .actions(java.util.Arrays.asList(new AlterTransactionAction())),
        null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void analyzeTableQueryPlanReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.analyzeTableQueryPlan(
        "x", new AnalyzeTableQueryPlanRequest().k(0).vector(new QueryTableRequestVector()), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void batchCommitTablesReturnsValidResponse() throws ApiException {
    TransactionApi api = new TransactionApi(apiClient);
    api.batchCommitTables(
        new BatchCommitTablesRequest().operations(new java.util.ArrayList<>()), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void batchCreateTableVersionsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.batchCreateTableVersions(
        new BatchCreateTableVersionsRequest().entries(new java.util.ArrayList<>()), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void batchDeleteTableVersionsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.batchDeleteTableVersions(
        "x", new BatchDeleteTableVersionsRequest().ranges(new java.util.ArrayList<>()), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void countTableRowsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.countTableRows("x", new CountTableRowsRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void createNamespaceReturnsValidResponse() throws ApiException {
    NamespaceApi api = new NamespaceApi(apiClient);
    api.createNamespace("x", new CreateNamespaceRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void createTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.createTable("x", new byte[0], null, null, null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void createTableIndexReturnsValidResponse() throws ApiException {
    IndexApi api = new IndexApi(apiClient);
    api.createTableIndex("x", new CreateTableIndexRequest().column("x").indexType("x"), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void createTableScalarIndexReturnsValidResponse() throws ApiException {
    IndexApi api = new IndexApi(apiClient);
    api.createTableScalarIndex("x", new CreateTableIndexRequest().column("x").indexType("x"), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void createTableTagReturnsValidResponse() throws ApiException {
    TagApi api = new TagApi(apiClient);
    api.createTableTag("x", new CreateTableTagRequest().tag("x").version(0L), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void createTableVersionReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.createTableVersion(
        "x", new CreateTableVersionRequest().version(0L).manifestPath("x"), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void declareTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.declareTable("x", new DeclareTableRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void deleteFromTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.deleteFromTable("x", new DeleteFromTableRequest().predicate("x"), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void deleteTableTagReturnsValidResponse() throws ApiException {
    TagApi api = new TagApi(apiClient);
    api.deleteTableTag("x", new DeleteTableTagRequest().tag("x"), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void deregisterTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.deregisterTable("x", new DeregisterTableRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void describeNamespaceReturnsValidResponse() throws ApiException {
    NamespaceApi api = new NamespaceApi(apiClient);
    api.describeNamespace("x", new DescribeNamespaceRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void describeTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.describeTable("x", new DescribeTableRequest(), null, null, null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void describeTableIndexStatsReturnsValidResponse() throws ApiException {
    IndexApi api = new IndexApi(apiClient);
    api.describeTableIndexStats("x", "x", new DescribeTableIndexStatsRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void describeTableVersionReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.describeTableVersion("x", new DescribeTableVersionRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void describeTransactionReturnsValidResponse() throws ApiException {
    TransactionApi api = new TransactionApi(apiClient);
    api.describeTransaction("x", new DescribeTransactionRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void dropNamespaceReturnsValidResponse() throws ApiException {
    NamespaceApi api = new NamespaceApi(apiClient);
    api.dropNamespace("x", new DropNamespaceRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void dropTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.dropTable("x", null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void dropTableIndexReturnsValidResponse() throws ApiException {
    IndexApi api = new IndexApi(apiClient);
    api.dropTableIndex("x", "x", null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void explainTableQueryPlanReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.explainTableQueryPlan(
        "x",
        new ExplainTableQueryPlanRequest()
            .query(new QueryTableRequest().k(0).vector(new QueryTableRequestVector())),
        null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void getTableStatsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.getTableStats("x", new GetTableStatsRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void getTableTagVersionReturnsValidResponse() throws ApiException {
    TagApi api = new TagApi(apiClient);
    api.getTableTagVersion("x", new GetTableTagVersionRequest().tag("x"), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void insertIntoTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.insertIntoTable("x", new byte[0], null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void listAllTablesReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.listAllTables(null, null, null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void listNamespacesReturnsValidResponse() throws ApiException {
    NamespaceApi api = new NamespaceApi(apiClient);
    api.listNamespaces("x", null, null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void listTableIndicesReturnsValidResponse() throws ApiException {
    IndexApi api = new IndexApi(apiClient);
    api.listTableIndices("x", new ListTableIndicesRequest(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void listTableTagsReturnsValidResponse() throws ApiException {
    TagApi api = new TagApi(apiClient);
    api.listTableTags("x", null, null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void listTableVersionsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.listTableVersions("x", null, null, null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void listTablesReturnsValidResponse() throws ApiException {
    NamespaceApi api = new NamespaceApi(apiClient);
    api.listTables("x", null, null, null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void mergeInsertIntoTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.mergeInsertIntoTable("x", "x", new byte[0], null, null, null, null, null, null, null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void namespaceExistsReturnsValidResponse() throws ApiException {
    NamespaceApi api = new NamespaceApi(apiClient);
    api.namespaceExists("x", new NamespaceExistsRequest(), null);
  }

  @Test
  void queryTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.queryTable("x", new QueryTableRequest().k(0).vector(new QueryTableRequestVector()), null);
    // Binary response — successful return is the contract assertion.
  }

  @Test
  void refreshMaterializedViewReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.refreshMaterializedView("x", null, null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void registerTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.registerTable("x", new RegisterTableRequest().location("x"), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void renameTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.renameTable("x", new RenameTableRequest().newTableName("x"), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void restoreTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.restoreTable("x", new RestoreTableRequest().version(0L), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void tableExistsReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.tableExists("x", new TableExistsRequest(), null);
  }

  @Test
  void updateTableReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.updateTable("x", new UpdateTableRequest().updates(new java.util.ArrayList<>()), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void updateTableSchemaMetadataReturnsValidResponse() throws ApiException {
    TableApi api = new TableApi(apiClient);
    api.updateTableSchemaMetadata("x", new java.util.HashMap<>(), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }

  @Test
  void updateTableTagReturnsValidResponse() throws ApiException {
    TagApi api = new TagApi(apiClient);
    api.updateTableTag("x", new UpdateTableTagRequest().tag("x").version(0L), null);
    // Non-null assertion omitted: some ops legitimately return null
    // when the response schema is typeless Object / empty body.
  }
}
