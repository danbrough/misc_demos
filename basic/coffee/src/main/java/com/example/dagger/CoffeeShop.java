package com.example.dagger;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
    modules = {
        HeaterModule.class,
        PumpModule.class
    }
)
public interface CoffeeShop {
  CoffeeMaker maker();

  CoffeeLogger logger();
}