import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.google.gson.Gson
import gr.uoa.di.JsonHelper
import gr.uoa.di.RestAPI
import gr.uoa.di.model.*
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static gr.uoa.di.RestAPI.BASE_URL

@Stepwise
class RestAPISpecification extends Specification {

  private static final int MOCK_SERVER_PORT = 9001
  private static final credentials = new Credentials("admin", "pass123!")

  @Shared
  WireMockServer wms
  @Shared
  RestAPI caller1 = new RestAPI("localhost", MOCK_SERVER_PORT, null)
  @Shared
  RestAPI caller2 = new RestAPI("localhost", MOCK_SERVER_PORT, new WebToken().getToken())

  def setupSpec() {
    wms = new WireMockServer(WireMockConfiguration.options().httpsPort(MOCK_SERVER_PORT))
    wms.start()
  }

  def cleanupSpec() {
    wms.stop()
  }

  def "T01. Admin logs in successfully"() {

    given:
    wms.givenThat(
      post(
        urlEqualTo("$BASE_URL" + "login")
      ).withRequestBody(
        equalToJson(new Gson().toJson(credentials))
      ).willReturn(
        okJson(new Gson().toJson(new WebToken()))
      )
    )

    when:
    caller1.login("admin", "pass123!")

    then:
    caller1.isLoggedIn()
  }

  def "T02. Import pois successfully"() {

    final var dataResult = DataResult.createExampleValue();

    given:
    wms.givenThat(
      post(
        urlEqualTo("$BASE_URL" + "import")
      )
        .withMultipartRequestBody(aMultipart()
                                    .withName("addresses.csv"))
        .willReturn(
          okJson(JsonHelper.gson.toJson(dataResult))
        )
    )

    when:
    DataResult result = caller2.importPoisCategories(new File("src/test/resources/addresses.csv"))

    then:
    result != null
    result == dataResult
  }

  def "T03. Search pois categories successfully"() {

    final var searchInput = SearchInput.createExampleValue();
    final var searchResponse = SearchResponse.createExampleValue();

    given:
    wms.givenThat(
      post(
        urlEqualTo("$BASE_URL" + "search/pois")
      )
        .withRequestBody(
          equalToJson(new Gson().toJson(searchInput))
        )
        .willReturn(
          okJson(JsonHelper.gson.toJson(searchResponse))
        )
    )

    when:
    SearchResponse result = caller2.searchPoisCategories(searchInput)

    then:
    result != null
    result == searchResponse
  }
}
