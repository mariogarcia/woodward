package woodward.extractors

import org.jsoup.nodes.Document
import woodward.Utils

/**
 *
 * @since 0.1.0
 */
class Text {

  /**
   * @since 0.1.0
   */
  static final HAPPY_PATHS = [
    ['attribute': 'itemprop', 'value': 'articlebody'],
    ['attribute': 'class', 'value': 'article-content']
  ]

  /**
   * @param document
   * @return
   * @since 0.1.0
   */
  static String extract(Document document) {
    def paths = [{-> HAPPY_PATHS.findResult(byHappyPath(document))},
                 {-> byImportance(document) }]


    paths.findResult(Utils.&skipIfEmptyList)
  }

  /**
   * @param document
   * @return
   * @since 0.1.0
   */
  static Closure byHappyPath(Document document) {
    return { Map<String, String> map ->
      return document
        .getElementsByAttribute(map.attribute)
        .findResult { element ->
        if (element.attr(map.attribute).toLowerCase() == map.value) {
          element.select("p").text()
        }
      }
    }
  }

  /**
   * @param document
   * @return
   * @since 0.1.0
   */
  static String byImportance(Document document) {
    return document
      .select("div p:gt(2)")
      .find()
      .parent()
      .select("p")
      .text()
  }
}
