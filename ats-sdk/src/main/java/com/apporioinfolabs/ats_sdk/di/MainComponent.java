package com.apporioinfolabs.ats_sdk.di;

import com.apporioinfolabs.ats_sdk.AtsLocationServiceClass;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MainModule.class)
public interface MainComponent {

    void inject(AtsLocationServiceClass atsLocationServiceClass);

}
