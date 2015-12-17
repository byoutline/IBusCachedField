package com.byoutline.observablecachedfield

import android.databinding.Observable
import com.byoutline.cachedfield.internal.DefaultExecutors
import com.byoutline.eventcallback.IBus
import com.byoutline.shadow.ObservableField
import com.google.common.util.concurrent.MoreExecutors
import spock.lang.Shared
import spock.lang.Unroll

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class ObservableCachedFieldWithArgSpec extends spock.lang.Specification {
    @Shared
    Map<Integer, String> argToValueMap = [1: 'a', 2: 'b']

    @Unroll
    def "should notify about new value: #val for arg: #arg"() {
        given:
        ObservableCachedFieldWithArg field = builder()
                .withValueProvider(MockFactory.getStringGetter(argToValueMap))
                .withoutEvents()
                .withCustomValueGetterExecutor(MoreExecutors.newDirectExecutorService())
                .build();
        boolean called = false
        def callback = new Observable.OnPropertyChangedCallback() {

            @Override
            void onPropertyChanged(Observable sender, int propertyId) {
                called = true
            }
        }
        ObservableField<String> obs = field.observable()
        obs.addOnPropertyChangedCallback(callback)

        when:
        field.postValue(arg)

        then:
        called
        obs.get() == val

        where:
        val | arg
        'a' | 1
        'b' | 2
    }

    private <RETURN_TYPE, ARG_TYPE> ObservableCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE> builder() {
        IBus bus = Mock()
        return new ObservableCachedFieldWithArgBuilder<RETURN_TYPE, ARG_TYPE>(MockFactory.getSameSessionIdProvider(), bus,
                DefaultExecutors.createDefaultValueGetterExecutor(), DefaultExecutors.createDefaultStateListenerExecutor())
    }
}
