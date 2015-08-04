package com.byoutline.ibuscachedfield;

import com.byoutline.cachedfield.CachedField;
import com.byoutline.cachedfield.ProviderWithArg;
import com.byoutline.cachedfield.dbcache.DbCachedValueProvider;
import com.byoutline.cachedfield.dbcache.DbWriter;
import com.byoutline.cachedfield.dbcache.FetchType;
import com.byoutline.eventcallback.ResponseEvent;
import com.byoutline.ibuscachedfield.internal.ErrorEvent;
import com.byoutline.ottocachedfield.internal.CachedFieldConstructorWrapper;

import javax.inject.Provider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Fluent interface builder of {@link CachedField}.
 *
 * @param <RETURN_TYPE> Type of object to be cached.
 * @param <BUS>         Type of bus that will be used to post events.
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public abstract class IBusCachedFieldBuilder<RETURN_TYPE, BUS> {

    private final CachedFieldConstructorWrapper<RETURN_TYPE, BUS> constructorWrapper;
    private Provider<RETURN_TYPE> valueGetter;
    private ResponseEvent<RETURN_TYPE> successEvent;
    private ErrorEvent errorEvent;
    private Provider<String> sessionIdProvider;
    private BUS bus;
    private ExecutorService valueGetterExecutor;
    private Executor stateListenerExecutor;

    public IBusCachedFieldBuilder(CachedFieldConstructorWrapper<RETURN_TYPE, BUS> constructorWrapper,
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

    public <API_RETURN_TYPE> DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE> withApiFetcher(Provider<API_RETURN_TYPE> apiValueProvider) {
        return new DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE>(apiValueProvider);
    }

    public static class DbCacheBuilderReader<API_RETURN_TYPE, RETURN_TYPE> {
        private final Provider<API_RETURN_TYPE> apiValueProvider;

        public DbCacheBuilderReader(Provider<API_RETURN_TYPE> apiValueProvider) {
            this.apiValueProvider = apiValueProvider;
        }

        public DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE> withDbWriter(DbWriter<API_RETURN_TYPE> dbSaver) {
            return new DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE>(apiValueProvider, dbSaver);
        }
    }

    public static class DbCacheBuilderWriter<API_RETURN_TYPE, RETURN_TYPE> {
        private final Provider<API_RETURN_TYPE> apiValueProvider;
        private final DbWriter<API_RETURN_TYPE> dbSaver;

        public DbCacheBuilderWriter(Provider<API_RETURN_TYPE> apiValueProvider, DbWriter<API_RETURN_TYPE> dbSaver) {
            this.apiValueProvider = apiValueProvider;
            this.dbSaver = dbSaver;
        }

        public OttoCachedFieldWithArgBuilder.SuccessEvent withDbReader(Provider<RETURN_TYPE> dbValueProvider) {
            ProviderWithArg<RETURN_TYPE, FetchType> valueProvider =
                    new DbCachedValueProvider<API_RETURN_TYPE, RETURN_TYPE>(apiValueProvider, dbSaver, dbValueProvider);
            return new OttoCachedFieldWithArgBuilder<RETURN_TYPE, FetchType>()
                    .withValueProvider(valueProvider);
        }
    }

    public class SuccessEvent {

        private SuccessEvent() {
        }

        public ErrorEventSetter withSuccessEvent(ResponseEvent<RETURN_TYPE> successEvent) {
            IBusCachedFieldBuilder.this.successEvent = successEvent;
            return new ErrorEventSetter();
        }
    }

    public class ErrorEventSetter {

        private ErrorEventSetter() {
        }

        public OverrideDefaultsSetter withGenericErrorEvent(Object errorEvent) {
            IBusCachedFieldBuilder.this.errorEvent = ErrorEvent.genericEvent(errorEvent);
            return new OverrideDefaultsSetter();
        }

        public OverrideDefaultsSetter withResponseErrorEvent(ResponseEvent<Exception> errorEvent) {
            IBusCachedFieldBuilder.this.errorEvent = ErrorEvent.responseEvent(errorEvent);
            return new OverrideDefaultsSetter();
        }

        public CachedField<RETURN_TYPE> build() {
            IBusCachedFieldBuilder.this.errorEvent = new ErrorEvent(null, null);
            return IBusCachedFieldBuilder.this.build();
        }
    }

    public class OverrideDefaultsSetter {

        private OverrideDefaultsSetter() {
        }

        public OverrideDefaultsSetter withCustomSessionIdProvider(Provider<String> sessionIdProvider) {
            IBusCachedFieldBuilder.this.sessionIdProvider = sessionIdProvider;
            return this;
        }

        public OverrideDefaultsSetter withCustomBus(BUS bus) {
            IBusCachedFieldBuilder.this.bus = bus;
            return this;
        }

        public OverrideDefaultsSetter withCustomValueGetterExecutor(ExecutorService valueGetterExecutor) {
            IBusCachedFieldBuilder.this.valueGetterExecutor = valueGetterExecutor;
            return this;
        }

        public OverrideDefaultsSetter withCustomStateListenerExecutor(Executor stateListenerExecutor) {
            IBusCachedFieldBuilder.this.stateListenerExecutor = stateListenerExecutor;
            return this;
        }

        public CachedField<RETURN_TYPE> build() {
            return IBusCachedFieldBuilder.this.build();
        }
    }

    public class Builder {

        private Builder() {
        }

        public CachedField<RETURN_TYPE> build() {
            return IBusCachedFieldBuilder.this.build();
        }
    }

    private CachedField<RETURN_TYPE> build() {
        return constructorWrapper.build(sessionIdProvider, valueGetter, successEvent, errorEvent, bus,
                valueGetterExecutor, stateListenerExecutor);
    }
}
