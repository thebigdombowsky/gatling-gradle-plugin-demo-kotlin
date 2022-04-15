package slhc

import io.gatling.javaapi.core.Simulation

import io.gatling.javaapi.core.*

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*


const val supplier2Port: String = "26781"
const val homeURL: String = "http://10.10.100.137:"

class SupplierSimulation : Simulation() {

      private val httpProtocol = http
            .baseUrl( homeURL + supplier2Port)
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
            .authorizationHeader("Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ3YThDbVUxaDM4RlpPdEdfbHF4dEtlTV9hMDZ6OTdRVGc2c19rTktRMjRnIn0.eyJleHAiOjE2MzkzMTk0MzMsImlhdCI6MTYzODQ1NTQzMywiYXV0aF90aW1lIjoxNjM4NDU1NDMzLCJqdGkiOiJmNmY2NTM3OC1lZjc5LTQxMDYtYjIzMC0yZmQyZmFmNmQyYjAiLCJpc3MiOiJodHRwczovL2l1aC1wZXJmLXNodmEudGhlZGV2Y2xvdWQubmV0L2F1dGgvcmVhbG1zL1N3aXNzbG9nIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImY6YzJmMjRmNGItZTM3ZC00M2E5LWI0M2EtMjNjNWFlZTM2ODU4OnN5c3RlbWFkbWluIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibWVkcG9ydGFsIiwibm9uY2UiOiI4ODgzN2I2OS0xMDg5LTQ2NmQtYTRkOS1mYzQ0ZDQyZTk0Y2QiLCJzZXNzaW9uX3N0YXRlIjoiYThhNWZjYjctYzBhNi00MzBkLWJkM2YtNDQ1NzQ2ZjIzYTM0IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJSZW1vdGVPcmRlcmluZ0FkbWluIiwiUmVtb3RlT3JkZXJpbmciLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiUGhhcm1hY3lNYW5hZ2VyIiwiU3dpc3Nsb2cgRVJQIFN1cHBvcnQiLCJQTVN5c3RlbUFkbWluIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgZW1haWwgcHJvZmlsZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InRlc3QgZHVtbXkiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzeXN0ZW1hZG1pbiIsImdpdmVuX25hbWUiOiJ0ZXN0IiwiZmFtaWx5X25hbWUiOiJkdW1teSIsImVtYWlsIjoidGVzdEBkdW1teS5jb20ifQ.irOJlBMzbOhTuNhPVjepnqmnSfyaeD8_BOF0j6CsBY08I4r71GEJgQZmMocMrIsduw2SR2zuwq7FH_tsFe0Ufv2HHOSsFG8UvNOJ4Oh3hm3RFYcMXd22xOh-MTaObiaR19T-GFrubZfnYyqPHWQSkfQRv3uZjh8nDU1xrHY5PY3oampKDWkFE1BBG2jTgBd9S6OEn0LF8EzYeZQWnHbiC_GDmcXsNNhjoRJsFXZMkzXx6H6qdQovCYmdEV5dpb800_0bTKUJ8M-3v7L59jTnZpQ_ydWcdkh5fAo3GRg8SmmXDu6fwu1LNrZKydGpaWM_o_DXZa9uYAZIxyC0rgqyHg")

        private val feeder = csv("getSuppliersOne.csv").random().circular()

        private val scn = scenario("slhc.Supplier2")
            .feed(feeder)
            .exec(http("getSuppliersOne")
                .get("/api/suppliers/one?supplierId=#{SupplierID}"))
            .pause(1)

        init {
            setUp(scn.injectOpen(atOnceUsers(10)).protocols(httpProtocol))
        }
    }


