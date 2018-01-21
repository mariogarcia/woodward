package woodward.plan

trait FilteredPlan<T> {

  abstract Collection<T> all()

  abstract T single()
}
