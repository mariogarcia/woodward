package woodward.extractors

import org.jsoup.nodes.Document

/**
 * Fetch the article title
 *
 * Assumptions:
 *
 * - title tag is the most reliable (inherited from Goose)
 * - h1, if properly detected, is the best (visible to users)
 * - og:title and h1 can help improve the title extraction
 *   versions, i.e. lowercase and ignoring special chars
 *
 * Explicit rules (TODO):
 *
 * 1. title == h1, no need to split
 * 2. h1 similar to og:title, use h1
 * 3. title contains h1, title contains og:title, len(h1) > len(og:title), use h1
 * 4. title starts with og:title, use og:title
 * 5. use title, after splitting
 *
 */
class Title {

  /**
   * Extracts a title from the {@link Document} passed as parameter
   *
   * @param document the document we want the title from
   * @return a string with the document's title
   * @since 0.1.0
   */
  static String extract(Document document) {
    def path = [{-> extractFromHead(document)},
                {-> extractFromH1(document)},
                {-> extractFromMeta(document)}]

    return path.findResult { cl -> cl() }
  }

  /**
   * Extracts a title from the {@link Document} head -> title tag
   *
   * @param document the document we want the title from
   * @return a string with the title found in the head section
   * @since 0.1.0
   */
  static String extractFromHead(Document document) {
    return document
      .select('title')
      .text()
  }

  /**
   * Extracts a title from the {@link Document} H1 tag
   *
   * @param document the document we want the title from
   * @return a string with the title found in the head section
   * @since 0.1.0
   */
  static String extractFromH1(Document document) {
    return document
      .select('h1')
      .collect { it.text() }
      .findAll { it.size() >= 2 }
      .max()
  }

  /**
   * Extracts a title from the {@link Document} meta tags
   *
   * @param document the document we want the title from
   * @return a string with the title found in the head section
   * @since 0.1.0
   */
  static String extractFromMeta(Document document) {
    def candidates = [
      'meta[property=og:title]',
      'meta[name=og:title]',
      'meta[itemprop=og:title]',
      'meta[itemprop=headline]'
    ]

    return candidates.find { String selector ->
      document.select(selector).attr('content')
    }
  }
}
