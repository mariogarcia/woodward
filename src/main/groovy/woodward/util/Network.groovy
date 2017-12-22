package woodward.util

import jodd.http.HttpBrowser
import jodd.http.HttpRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Utility functions to deal with http requests
 *
 * @since 0.1.0
 */
class Network {

  /**
   * Retrieves a given page located at the url passed as parameter and
   * parses the result as an instance of {@link Document} to make it
   * easier to traverse the document
   *
   * @param url the document location
   * @return an instance of {@link Document}
   * @since 0.1.0
   * @see Document
   */
  static Document getDocument(String url) {
    HttpRequest request = HttpRequest.get(url)
    HttpBrowser browser = new HttpBrowser()

    browser.sendRequest(request)

    return Jsoup.parse(browser.page, url)
  }
}
