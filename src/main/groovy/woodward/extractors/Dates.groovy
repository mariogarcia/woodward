package woodward.extractors

import groovy.transform.CompileDynamic
import org.jsoup.nodes.Document

/**
 * Functions to extract dates from a given article
 *
 * @since 0.1.0
 */
class Dates {

  /**
   * This regular expression will be used to extract a date from a
   * given string
   *
   * @since 0.1.0
   */
  static final DATE_REGEX = /([\.\/\-_]{0,1}(19|20)\d{2})[\.\/\-_]{0,1}(([0-3]{0,1}[0-9][\.\/\-_])|(\w{3,5}[\.\/\-_]))([0-3]{0,1}[0-9][\.\/\-]{0,1})?/

  /**
   * List of combinations representing attribute names and expected
   * values of html elements. First two keys serve to locate the
   * element, and the third key is the name of the attribute to get
   * the date from.
   *
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
   * The article publish date
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
     * Extracts the publish date from the uri used to
     * get the article.
     *
     * @param document document to get the date from
     * @return a string representing the extracted date
     * @since 0.1.0
     */
    @CompileDynamic
    static String fromUrl(Document document) {
      def url = document.location()
      def matcher = url =~ DATE_REGEX
      def urlDate = matcher.hasGroup() & matcher.size() > 1 ?
        matcher[0].first()[1..-2] :
        ""

      return urlDate
    }

    /**
     * After the uri, you could also get the publish date looking into
     * the metatag elements
     *
     * @param document document to get the date from
     * @return a string representing the article publish date
     * @since 0.1.0
     */
    static String fromMetatags(Document document) {
      DATE_TAGS.findResult(PublishDate.loopThroughOptions(document))
    }

    /**
     * Method returning a function used to convert every element of a
     * list from a map to a string containing a date
     *
     * @param document document to get the date from
     * @return a function having a map as parameter and returning a
     * string
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
