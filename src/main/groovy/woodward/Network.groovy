package woodward

import jodd.http.HttpRequest
import jodd.http.HttpResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * @since 0.1.0
 */
class Network {

  /**
   * @param url
   * @return
   * @since 0.1.0
   */
  static Document getDocument(String url) {
    String html = HttpRequest
      .get(url)
      .send()
      .bodyText()

    return Jsoup.parse(html, url)
  }
}
