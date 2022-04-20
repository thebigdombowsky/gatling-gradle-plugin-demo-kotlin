@file:Suppress("UnusedImport")

package slhc

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers
import io.gatling.javaapi.core.CoreDsl.rampConcurrentUsers
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http

class ConcurrentSupplierSimulation : Simulation() {

    private val feederSuppliers = CoreDsl.csv("getSuppliersOne.csv").random().circular()
    private val feederSupplierItems = CoreDsl.csv("getSupplierItemsOne.csv").random().circular()

    private val httpProtocol = http
        .baseUrl( "http://10.10.100.137:27812" )
        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("en-US,en;q=0.5")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
        .authorizationHeader("Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ3YThDbVUxaDM4RlpPdEdfbHF4dEtlTV9hMDZ6OTdRVGc2c19rTktRMjRnIn0.eyJleHAiOjE2MzkzMTk0MzMsImlhdCI6MTYzODQ1NTQzMywiYXV0aF90aW1lIjoxNjM4NDU1NDMzLCJqdGkiOiJmNmY2NTM3OC1lZjc5LTQxMDYtYjIzMC0yZmQyZmFmNmQyYjAiLCJpc3MiOiJodHRwczovL2l1aC1wZXJmLXNodmEudGhlZGV2Y2xvdWQubmV0L2F1dGgvcmVhbG1zL1N3aXNzbG9nIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImY6YzJmMjRmNGItZTM3ZC00M2E5LWI0M2EtMjNjNWFlZTM2ODU4OnN5c3RlbWFkbWluIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibWVkcG9ydGFsIiwibm9uY2UiOiI4ODgzN2I2OS0xMDg5LTQ2NmQtYTRkOS1mYzQ0ZDQyZTk0Y2QiLCJzZXNzaW9uX3N0YXRlIjoiYThhNWZjYjctYzBhNi00MzBkLWJkM2YtNDQ1NzQ2ZjIzYTM0IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJSZW1vdGVPcmRlcmluZ0FkbWluIiwiUmVtb3RlT3JkZXJpbmciLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiUGhhcm1hY3lNYW5hZ2VyIiwiU3dpc3Nsb2cgRVJQIFN1cHBvcnQiLCJQTVN5c3RlbUFkbWluIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgZW1haWwgcHJvZmlsZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InRlc3QgZHVtbXkiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzeXN0ZW1hZG1pbiIsImdpdmVuX25hbWUiOiJ0ZXN0IiwiZmFtaWx5X25hbWUiOiJkdW1teSIsImVtYWlsIjoidGVzdEBkdW1teS5jb20ifQ.irOJlBMzbOhTuNhPVjepnqmnSfyaeD8_BOF0j6CsBY08I4r71GEJgQZmMocMrIsduw2SR2zuwq7FH_tsFe0Ufv2HHOSsFG8UvNOJ4Oh3hm3RFYcMXd22xOh-MTaObiaR19T-GFrubZfnYyqPHWQSkfQRv3uZjh8nDU1xrHY5PY3oampKDWkFE1BBG2jTgBd9S6OEn0LF8EzYeZQWnHbiC_GDmcXsNNhjoRJsFXZMkzXx6H6qdQovCYmdEV5dpb800_0bTKUJ8M-3v7L59jTnZpQ_ydWcdkh5fAo3GRg8SmmXDu6fwu1LNrZKydGpaWM_o_DXZa9uYAZIxyC0rgqyHg")

        private val scnSuppliers = CoreDsl.scenario("Supplier2")
        .feed(feederSuppliers)
        .exec(http("getSuppliersOne")
            .get("/api/suppliers/one?supplierId=#{SupplierID}"))
        .pause(1)

        private val scnSupplierItems = CoreDsl.scenario("SupplierItems")
        .feed(feederSupplierItems)
        .exec(http("getSupplierItemsOne")
            .get("/api/suppliers/one/accounts/one/items/one?supplierId=#{supplier_id}&accountId=#{account_id}&reorderNumber=#{reorder_number}"))
        .pause(1)

        private val scnSuppliersAll = CoreDsl.scenario("SuppliersAll")
        .feed(feederSuppliers)
        .exec(http("getSuppliersAll")
            .get("/api/suppliers"))
        .pause(1)

    init {
        setUp(

            scnSuppliers.injectClosed(
                rampConcurrentUsers(1).to(50).during(60),
                constantConcurrentUsers(50).during(3600)
            ).protocols(httpProtocol),

            scnSuppliersAll.injectClosed(
                rampConcurrentUsers(1).to(50).during(60),
                constantConcurrentUsers(50).during(3600)
            ).protocols(httpProtocol),

            scnSupplierItems.injectClosed(
                rampConcurrentUsers(1).to(50).during(60),
                constantConcurrentUsers(50).during(3600)
            ).protocols(httpProtocol)

            /*scnSuppliers.injectOpen(
                CoreDsl.nothingFor(4), // 1
                CoreDsl.atOnceUsers(10), // 2
                CoreDsl.rampUsers(10).during(5), // 3
                CoreDsl.constantUsersPerSec(1.0).during(15), // 4
                CoreDsl.constantUsersPerSec(1.0).during(15).randomized(), // 5
                CoreDsl.rampUsersPerSec(1.0).to(20.0).during(10), // 6
                CoreDsl.rampUsersPerSec(1.0).to(20.0).during(10).randomized(), // 7
                CoreDsl.stressPeakUsers(50).during(300) // 8
            ).protocols(httpProtocol),

            scnSupplierItems.injectOpen(
                CoreDsl.nothingFor(4), // 1
                CoreDsl.atOnceUsers(10), // 2
                CoreDsl.rampUsers(10).during(5), // 3
                CoreDsl.constantUsersPerSec(1.0).during(15), // 4
                CoreDsl.constantUsersPerSec(1.0).during(15).randomized(), // 5
                CoreDsl.rampUsersPerSec(1.0).to(20.0).during(10), // 6
                CoreDsl.rampUsersPerSec(1.0).to(20.0).during(10).randomized(), // 7
                CoreDsl.stressPeakUsers(50).during(300) // 8
            ).protocols(httpProtocol),


            scnSuppliersAll.injectOpen(
                CoreDsl.nothingFor(4), // 1
                CoreDsl.atOnceUsers(10), // 2
                CoreDsl.rampUsers(10).during(5), // 3
                CoreDsl.constantUsersPerSec(1.0).during(15), // 4
                CoreDsl.constantUsersPerSec(1.0).during(15).randomized(), // 5
                CoreDsl.rampUsersPerSec(1.0).to(20.0).during(10), // 6
                CoreDsl.rampUsersPerSec(1.0).to(20.0).during(10).randomized(), // 7
                CoreDsl.stressPeakUsers(50).during(300) // 8
            ).protocols(httpProtocol)*/
        )

    }

}