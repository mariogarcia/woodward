package woodward.extractors

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import woodward.Source
import woodward.Category
import woodward.ArticleHolder
import woodward.util.URIs
import woodward.util.Misc
import woodward.util.Network

/**
 * A category represents a group of articles sharing the same topic
 *
 * @since 0.1.0
 */
class Categories {

  /**
   * Extracts a list of {@link Category} from the {@link Document}
   * passed as parameter
   *
   * @param document document we want the categories from
   * @return a list of {@link Category} instances
   * @since 0.1.0
   */
  static List<Category> extract(Document document) {
    URI uri = URI.create(document.location())
    String host = uri.host - "www."
    String selector = "a[href*=${host}]"

    return document
      .select(selector)
      .findAll(Categories.&isCategoryLink)
      .collect(Categories.&extractCategoryFromLink)
  }

  /**
   * Whether the {@link Element} passed as parameter represents
   * a category link or not.
   *
   * A category link should follow certain rules:
   *
   * - The link name should have only one word (Sports,
   *   Technology...etc)
   * - The uri should have only one fragment in its path
   * - Should not have a query part
   *
   * @param link an instance of {@link Element}
   * @return whether the link is from a category or not
   * @since 0.1.0
   */
  static Boolean isCategoryLink(Element link) {
    Optional<URI> uri = URIs.parseURI(link.attr('href'))
    Integer pathSize = URIs.getPathSize(uri)
    Boolean hasQuery = URIs.hasQuery(uri)
    Boolean hasNoun = hasCategoryNoun(link.text())

    return pathSize == 1 && !hasQuery && hasNoun
  }

  /**
   * A category normally is named by just one word: Sports,
   * International...
   *
   * @param sentence sentence where the possible category name is
   * @return whether there is just one word (true) or more (false) to
   * name the category
   * @since 0.1.0
   */
  static Boolean hasCategoryNoun(String sentence) {
    return sentence
      .split(" ")
      .findAll()
      .size() == 1
  }

  /**
   * Creates a {@link Category} instance from the {@link Element}
   * passed as parameter
   *
   * @param link an element representing an html link
   * @return an instance of type {@link Category}
   * @since 0.1.0
   */
  static Category extractCategoryFromLink(Element link) {
    def href = URIs.repairLink(link.attr('href'), link)
    def name = link.text()
    def articles = getArticlesFrom(href)

    return new Category(name: name, link: href, articles: articles)
  }

  /**
   * Gathers all {@link ArticleHolder} found in the link passed as
   * parameter
   *
   * @param link link to get the article list from
   * @return a list of {@link ArticleHolder} instances
   * @since 0.1.0
   */
  static List<ArticleHolder> getArticlesFrom(String link) {
    Document categoryHtml = Network.getDocument(link)

    return categoryHtml
      .select('a')
      .collect(Categories.&toArticleHolder)
  }

  /**
   * Converts a given {@link Element} representing a potential
   * article to a {@link ArticleHolder}
   *
   * @param element an element representing an {@link ArticleHolder}
   * @return an instance of type {@link ArticleHolder}
   * @since 0.1.0
   */
  static ArticleHolder toArticleHolder(Element element) {
    return new ArticleHolder(link: element.attr('href'))
  }
}
