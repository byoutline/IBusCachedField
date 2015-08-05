package com.byoutline.ibuscachedfield.builders;

import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg;
import com.byoutline.ibuscachedfield.events.ResponseEventWithArg;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @param <RETURN_TYPE> Type of object to be cached.
 * @param <ARG_TYPE>    Type of argument that needs to be passed to calculate value.
 * @param <BUS>         type of Bus that can be used to post events.
 *                      {@link CachedEndpointWithArg} build.
 */
public interface CachedFieldWithArgConstructorWrapper<RETURN_TYPE, ARG_TYPE, BUS> {
    CachedFieldWithArg<RETURN_TYPE, ARG_TYPE>
    build(Provider<String> sessionIdProvider,
          ProviderWithArg<RETURN_TYPE, ARG_TYPE> valueGetter,
          ResponseEventWithArg<RETURN_TYPE, ARG_TYPE> successEvent,
          ResponseEventWithArg<Exception, ARG_TYPE> errorEvent,
          BUS bus,
          ExecutorService valueGetterExecutor, Executor stateListenerExecutor);
}
