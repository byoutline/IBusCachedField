package com.byoutline.ibuscachedfield.builders;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.cachedendpoint.CachedEndpointWithArg;
import com.byoutline.eventcallback.ResponseEvent;
import com.byoutline.ibuscachedfield.internal.ErrorEvent;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @param <RETURN_TYPE> Type of object to be cached.
 * @param <BUS>         type of Bus that can be used to post events.
 *                      {@link CachedEndpointWithArg} build.
 */
public interface CachedFieldConstructorWrapper<RETURN_TYPE, BUS> {
    CachedField<RETURN_TYPE> build(Provider<String> sessionIdProvider,
                                   Provider<RETURN_TYPE> valueGetter,
                                   ResponseEvent<RETURN_TYPE> successEvent, ErrorEvent errorEvent,
                                   BUS bus,
                                   ExecutorService valueGetterExecutor, Executor stateListenerExecutor);
}
