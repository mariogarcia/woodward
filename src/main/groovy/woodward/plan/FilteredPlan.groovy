package woodward.plan

trait FilteredPlan<T> {

  abstract List<T> all()

  abstract T single()
}
