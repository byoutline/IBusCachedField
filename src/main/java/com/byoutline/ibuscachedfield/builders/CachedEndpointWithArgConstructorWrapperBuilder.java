package com.byoutline.ibuscachedfield.builders;

import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg;
import com.byoutline.cachedfield.cachedendpoint.StateAndValue;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @param <RETURN_TYPE> Type of object to be cached.
 * @param <ARG_TYPE>    Type of argument that needs to be passed to make an API call.
 * @param <BUS>         type of Bus that can be used to post events.
 *                      {@link com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg} build.
 */
public interface CachedEndpointWithArgConstructorWrapperBuilder<RETURN_TYPE, ARG_TYPE, BUS> {
    CachedEndpointWithArg<RETURN_TYPE, ARG_TYPE> build(Provider<String> sessionIdProvider,
                                                       ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
                                                       ResponseEventWithArg<StateAndValue<RETURN_TYPE, ARG_TYPE>, ARG_TYPE> resultEvent,
                                                       BUS bus, ExecutorService valueGetterExecutor, Executor stateListenerExecutor);
}
