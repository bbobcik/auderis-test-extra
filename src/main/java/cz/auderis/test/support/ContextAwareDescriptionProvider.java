package cz.auderis.test.support;

/**
 * Description providers (notably implementations of {@link DescriptionProvider} and
 * {@link MismatchDescriptionProvider}) may implement this interface in order to
 * access a description context. This context is an arbitrary, application-defined
 * object, established by a call to {@link NaturalDescriptionJoiner#withDescriptionContext(Object)}.
 */
public interface ContextAwareDescriptionProvider {

    /**
     * Makes the description provider aware of current context. This method is invoked before
     * each call to appropriate {@code describe()} method.
     *
     * @param descriptionContext context defined by {@link NaturalDescriptionJoiner#withDescriptionContext(Object)}
     */
    void setContext(Object descriptionContext);

}
