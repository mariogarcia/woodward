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
   * Extracts a single category from the document passed as parameter
   * with a specific name
   *
   * @param document the source document
   * @param category the name of the category we are looking for
   * @return an instance of type {@link Category}
   * @since 0.1.0
   */
  static Category extractCategory(Document document, String category) {
    URI uri = URI.create(document.location())
    String host = uri.host - "www."
    String selector = "a[href*=${host}]"

    return document
      .select(selector)
      .find(Categories.isCategoryLinkAndHasName(category))
      .collect(Categories.&extractCategoryFromLink)
      .find()
  }

  /**
   * Creates a predicate able to find those {@link Element} instances
   * that represents a category link and containing the category name
   * passed as parameter
   *
   * @param category the category name
   * @return a predicate to find a specific set of links
   * @since 0.1.0
   */
  static Closure<Boolean> isCategoryLinkAndHasName(String category) {
    return Misc.and(Categories.&isCategoryLink, Categories.hasName(category))
  }

  /**
   * Represents a predicate to find {@link Element} instances having in their
   * text the category passed as parameter
   *
   * Both element text and category name are transformed to lowercase and then
   * the function checks that the category passed as parameter is contained in the
   * link text
   *
   * @param category the name of the category
   * @return a predicate to check whether the category name is
   * contained in the element's text
   * @since 0.1.0
   */
  static Closure<Boolean> hasName(String category) {
    return { Element item ->
      return item
        .text()
        .toLowerCase()
        .trim()
        .contains(category.toLowerCase().trim())
    }
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
      .findAll(Categories.belongToSameCategoryAs(link))
      .collect(Categories.&toArticleHolder)
  }

  /**
   * This function returns a predicate that can be used to check
   * whether a given {@link Element} representing a link, belongs to
   * the same category as the one represented by the string uri passed
   * as parameter
   *
   * @param categoryUri uri of the category
   * @return a predicate to check whether the {@link Element} passed
   * as parameter belongs to the same category by checking its URI
   * root path
   * @since 0.1.0
   */
  static Closure<Boolean> belongToSameCategoryAs(String categoryUri) {
    Optional<String> optionalURI = Optional.ofNullable(categoryUri)

    if (!optionalURI.isPresent()) {
      //log.warn('category uri is not present, predicate {-> false } returned instead')

      return { Element element ->
        false
      }
    }

    String categoryPath = URIs.getRootPathFrom(optionalURI)

    return { Element element ->
      return categoryPath == URIs.getRootPathFrom(Optional.ofNullable(element.attr('href')))
    }
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
