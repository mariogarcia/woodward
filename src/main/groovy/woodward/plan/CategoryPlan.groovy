package woodward.plan

import org.jsoup.nodes.Document
import woodward.Category
import woodward.util.Domain
import woodward.util.Network
import woodward.extractors.Categories

/**
 * Plan responsible to load categories found in a given site
 *
 * @since 0.1.0
 */
@Domain(copyWith = true)
class CategoryPlan implements FilteredPlan<Category> {

  /**
   * URI where to find categories
   *
   * @since 0.1.0
   */
  String uri

  /**
   * Regex used to filter out the categories by name
   *
   * @since 0.1.0
   */
  String nameRegex

  /**
   * If true the plan won't load categories related articles
   *
   * @since 0.1.0
   */
  Boolean justNames

  /**
   * Instructs the plan to filter categories by the regex passed as
   * parameter. It will be evaluated against the categories names
   *
   * @param regex regular expression to filter categories by name
   * @return a new {@link CategoryPlan} plan filtered by regex
   * @since 0.1.0
   */
  CategoryPlan byName(String regex) {
    return this.copyWith(nameRegex: regex)
  }

  /**
   * Instructs the plan not to load related articles
   *
   * @return a new {@link CategoryPlan} but without loading related
   * articles
   * @since 0.1.0
   */
  CategoryPlan justNames() {
    return this.copyWith(justNames: true)
  }

  /**
   * Executes the plan and returns all categories found by this plan
   *
   * @return all categories found by this plan
   * @since 0.1.0
   */
  List<Category> all() {
    Document document = Network.getDocument(uri)
    List<Category> categories = Categories.extractNames(document)

    return categories
      .findAll(Categories.byNameIfPresent(nameRegex))
      .collect(Categories.loadArticlesIf(!justNames))
  }

  /**
   * Executes the plan but only returns the first {@link Category}
   * found by this plan
   *
   * @return first found {@link Category} by this plan
   * @since 0.1.0
   */
  Category single() {
    return all().find()
  }
}
