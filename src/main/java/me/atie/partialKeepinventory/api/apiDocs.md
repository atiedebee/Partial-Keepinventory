# Mod Integration

As an example for using the API, you can look in ``impl/trinkets`` for usage of the features below.

## Entrypoint

For mod integration you can use the entrypoint "partial-keepinv" and add it to your fabric.mod.json:

```json
"entrypoints": {
    "partial-keepinv": [
      "my.example.class"
    ]
}
```

## Initializer class

The entrypoint class will extends the ``pkiApi`` class. 

```java
import me.atie.partialKeepinventory.api.pkiApi;

// Base class containing all required overrides
public class Example extends pkiApi {
    public Example(){
    }

    @Override
    public String getModId() {
        return "modid";
    }
}
```

getModId is a required override. This should just return the mods mod id.

## Changing Drop Behaviour

The ``pkiApi`` class contains the ``getDropBehaviour`` method as shown here:
```java
public Pair<Double, InventoryUtil.DropAction> getDropBehaviour(PlayerEntity player, ItemStack itemStack){
    if( itemStack.getItem().equals( myItem ) ){
        return new Pair(1.0, DropAction.DESTROY);
    }
    return null;
}
```

You get the player that died and an ItemStack with the count set to how many of said item were found in the inventory. You can use these two to return a pair containing a ``double`` in the range ``0.0 - 1.0`` to indicate the percentage of items to be dropped, and a DropAction that indicates what to do with the aforementioned percentage of items. Possible values are:
- ``DROP``
  - Drops the items. 
- ``KEEP``
  - Ignores the percentage and keeps the item 
- ``DESTROY``
  - Destroys the items.
- ``NONE``
    - Ignores the values returned and lets the mod / other mods calculate the droprate and action.

The function will be ignored if it returns either ``NONE`` as the drop action or ``null``.

## Providing Inventory Slots
If your mod adds slots to the inventory that should be dropped together with the rest of the inventory, you can override the ``getInventorySlots`` function. This returns a list of ``ItemStack`` in the inventory that should be dropped.

```java
    public List<ItemStack> getInventorySlots(PlayerEntity player){
        return List.of();
    }
```

The default return value is ``null`` or ``List.of()``. You can return these yourself or call the super function.

## Providing Settings
You can integrate a settings class with settings regarding your mod's implementation by creating a class implementing the ``pkiSettingsApi`` interface and implementing it. You can return it by overriding ``getSettings``. If you do this the mod itself will sync settings between client and server. You can sync the config by calling ``pkiSettings.updateServerConfig();``.