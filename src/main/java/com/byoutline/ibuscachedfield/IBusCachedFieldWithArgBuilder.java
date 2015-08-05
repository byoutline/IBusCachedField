package com.byoutline.ibuscachedfield;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.eventcallback.ResponseEvent;
import com.byoutline.ibuscachedfield.builders.CachedFieldConstructorWrapper;
import com.byoutline.ibuscachedfield.internal.ErrorEvent;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Fluent interface builder of {@link CachedField}.
 *
 * @param <RETURN_TYPE> Type of object to be cached.
 * @param <ARG_TYPE>    Type of argument that needs to be passed to calculate value.
 * @param <BUS>         Type of bus that will be used to post events.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public abstract class IBusCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE, BUS> {

    private final CachedFieldConstructorWrapper<RETURN_TYPE, BUS> constructorWrapper;
    private Provider<RETURN_TYPE> valueGetter;
    private ResponseEvent<RETURN_TYPE> successEvent;
    private ErrorEvent errorEvent;
    private Provider<String> sessionIdProvider;
    private BUS bus;
    private ExecutorService valueGetterExecutor;
    private Executor stateListenerExecutor;

    public IBusCachedFieldWithArgBuilder(CachedFieldConstructorWrapper<RETURN_TYPE, BUS> constructorWrapper,
                                         BUS defaultBus,
                                         Provider<String> defaultSessionIdProvider,
                                         ExecutorService defaultValueGetterExecutor,
                                         Executor defaultStateListenerExecutor) {
        this.constructorWrapper = constructorWrapper;
        bus = defaultBus;
        sessionIdProvider = defaultSessionIdProvider;
        valueGetterExecutor = defaultValueGetterExecutor;
        stateListenerExecutor = defaultStateListenerExecutor;
    }

    public SuccessEvent withValueProvider(Provider<RETURN_TYPE> valueProvider) {
        this.valueGetter = valueProvider;
        return new SuccessEvent();
    }

    public class SuccessEvent {

        private SuccessEvent() {
        }

        public ErrorEventSetter withSuccessEvent(ResponseEvent<RETURN_TYPE> successEvent) {
            IBusCachedFieldWithArgBuilder.this.successEvent = successEvent;
            return new ErrorEventSetter();
        }
    }

    public class ErrorEventSetter {

        private ErrorEventSetter() {
        }

        public OverrideDefaultsSetter withGenericErrorEvent(Object errorEvent) {
            IBusCachedFieldWithArgBuilder.this.errorEvent = ErrorEvent.genericEvent(errorEvent);
            return new OverrideDefaultsSetter();
        }

        public OverrideDefaultsSetter withResponseErrorEvent(ResponseEvent<Exception> errorEvent) {
            IBusCachedFieldWithArgBuilder.this.errorEvent = ErrorEvent.responseEvent(errorEvent);
            return new OverrideDefaultsSetter();
        }

        public CachedField<RETURN_TYPE> build() {
            IBusCachedFieldWithArgBuilder.this.errorEvent = new ErrorEvent(null, null);
            return IBusCachedFieldWithArgBuilder.this.build();
        }
    }

    public class OverrideDefaultsSetter {

        private OverrideDefaultsSetter() {
        }

        public OverrideDefaultsSetter withCustomSessionIdProvider(Provider<String> sessionIdProvider) {
            IBusCachedFieldWithArgBuilder.this.sessionIdProvider = sessionIdProvider;
            return this;
        }

        public OverrideDefaultsSetter withCustomBus(BUS bus) {
            IBusCachedFieldWithArgBuilder.this.bus = bus;
            return this;
        }

        public OverrideDefaultsSetter withCustomValueGetterExecutor(ExecutorService valueGetterExecutor) {
            IBusCachedFieldWithArgBuilder.this.valueGetterExecutor = valueGetterExecutor;
            return this;
        }

        public OverrideDefaultsSetter withCustomStateListenerExecutor(Executor stateListenerExecutor) {
            IBusCachedFieldWithArgBuilder.this.stateListenerExecutor = stateListenerExecutor;
            return this;
        }

        public CachedField<RETURN_TYPE> build() {
            return IBusCachedFieldWithArgBuilder.this.build();
        }
    }

    public class Builder {

        private Builder() {
        }

        public CachedField<RETURN_TYPE> build() {
            return IBusCachedFieldWithArgBuilder.this.build();
        }
    }

    private CachedField<RETURN_TYPE> build() {
        return constructorWrapper.build(sessionIdProvider, valueGetter, successEvent, errorEvent, bus,
                valueGetterExecutor, stateListenerExecutor);
    }
}
