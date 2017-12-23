package woodward.util

import spock.lang.Specification

/**
 * Tests miscelaneous functions
 *
 * @since 0.1.0
 */
class MiscSpec extends Specification {

  def 'use skipIfEmptyList with findResult'() {
    given: 'a list of executions'
    def executions = [{-> null},
                      {-> 1 == 2 ? "yeah 1 == 2" : null},
                      {-> 1 == 1 ? "yeah 1 == 1": null}]

    when: 'looking for the first result that doesnt return null'
    def result = executions.findResult(Misc.&skipIfEmptyList)

    then: 'we should get the first result and stop evaluation'
    result == "yeah 1 == 1"
  }
}
