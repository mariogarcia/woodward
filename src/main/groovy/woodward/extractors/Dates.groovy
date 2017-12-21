package woodward.extractors

import org.jsoup.nodes.Document

/**
 * @since 0.1.0
 */
class Dates {

  /**
   * @since 0.1.0
   */
  static final DATE_REGEX = /([\.\/\-_]{0,1}(19|20)\d{2})[\.\/\-_]{0,1}(([0-3]{0,1}[0-9][\.\/\-_])|(\w{3,5}[\.\/\-_]))([0-3]{0,1}[0-9][\.\/\-]{0,1})?/

  /**
   * @since 0.1.0
   */
  static final DATE_TAGS = [
    ['attribute': 'property', 'value': 'rnews:datePublished',
     'content': 'content'],
    ['attribute': 'property', 'value': 'article:published_time',
     'content': 'content'],
    ['attribute': 'name', 'value': 'OriginalPublicationDate',
     'content': 'content'],
    ['attribute': 'itemprop', 'value': 'datePublished',
     'content': 'datetime'],
    ['attribute': 'property', 'value': 'og:published_time',
     'content': 'content'],
    ['attribute': 'name', 'value': 'article_date_original',
     'content': 'content'],
    ['attribute': 'name', 'value': 'publication_date',
     'content': 'content'],
    ['attribute': 'name', 'value': 'sailthru.date',
     'content': 'content'],
    ['attribute': 'name', 'value': 'PublishDate',
     'content': 'content'],
    ['attribute': 'pubdate', 'value': 'pubdate',
     'content': 'datetime'],
  ]

  /**
   *
   *
   * @since 0.1.0
   *
   */
  static class PublishDate {

    /**
     * 3 strategies for publishing date extraction. The strategies
     * are descending in accuracy and the next strategy is only
     * attempted if a preferred one fails.
     *
     * 1. Pubdate from URL
     * 2. Pubdate from metadata
     * 3. Raw regex searches in the HTML + added heuristics
     *
     * @param document
     * @return
     * @since 0.1.0
     */
    static String extract(Document document) {
      return fromUrl(document) ?: fromMetatags(document)
    }

    /**
     * @param document
     * @return
     * @since 0.1.0
     */
    static String fromUrl(Document document) {
      def url = document.location()
      def matcher = url =~ DATE_REGEX
      def urlDate = matcher.hasGroup() & matcher.size() > 1 ?
        matcher[0].first()[1..-2] :
        ""

      return urlDate
    }

    /**
     * @param document
     * @return
     * @since 0.1.0
     */
    static String fromMetatags(Document document) {
      DATE_TAGS.findResult(PublishDate.loopThroughOptions(document))
    }

    /**
     * @param document
     * @return
     * @since 0.1.0
     */
    static Closure loopThroughOptions(Document document) {
      return { Map<String, String> map ->
        return document
          .getElementsByAttribute(map.attribute)
          .findResult { element ->
            if (element.attr(map.attribute) == map.value) {
              element.attr(map.content)
            }
          }
      }
    }
  }
}
