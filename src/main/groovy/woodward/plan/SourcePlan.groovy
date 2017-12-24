package woodward.plan

import org.jsoup.nodes.Document
import woodward.Source
import woodward.util.Misc
import woodward.util.Network
import woodward.extractors.Categories

class SourcePlan implements Plan<Source> {

  String uri

  /**
   * This method blocks until the value is computed
   *
   * return the result of the plan
   * @since 0.1.0
   */
  Source get() {
    def doc = Network.getDocument(uri)
    def computations = [{-> Categories.extract(doc)}]

    def (categories) = computations
      .parallelStream()
      .map(Misc.executeClosure())
      .collect()

    return new Source(categories: categories)
  }
}
