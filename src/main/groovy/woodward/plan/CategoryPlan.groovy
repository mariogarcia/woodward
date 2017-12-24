package woodward.plan

import woodward.Category
import woodward.util.Domain
import woodward.util.Network
import woodward.extractors.Categories

/**
 * @since 0.1.0
 */
@Domain(copyWith = true)
class CategoryPlan implements FilteredPlan<Category> {

  /**
   * @since 0.1.0
   */
  String uri

  /**
   * @since 0.1.0
   */
  String name

  /**
   * @param category
   * @return
   * @since 0.1.0
   */
  CategoryPlan byName(String category) {
    return this.copyWith(name: category)
  }

  /**
   * @return
   * @since 0.1.0
   */
  List<Category> all() {
    def document = Network.getDocument(uri)

    return name.trim() ?
      Categories
      .extractCategory(document, name)
      .collect() : Categories.extract(document)
  }

  /**
   * @return
   * @since 0.1.0
   */
  Category single() {
    return all().find()
  }
}
