package ru.edenor.customAttributePotion.data

interface Storage {
  fun getPotions(): List<Potion>

  fun getPotion(name: String): Potion?

  fun reload()
}
