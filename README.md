Shopshare plugin for ChestShop and GriefPrevention to allow multiple players to access the same shop.

## Shopshare Lists
Players have local and global shopshare lists. 
#### Local Shopshare Lists
Local shopshare lists exist within the bounds of a claim or subdivision and only effect shop chests within the bounds of that claim or subdivision. Local shop chests can only be opened if the one trying to open it also has container trust or build trust permissions in the claim.

#### Global Shopshare Lists
Global shopshare lists exist across the entire server. Players can access their global shopshare list by using /shopshare commands outside of a claim. Global shopshare permissions allow players to open both shop chests that are unclaimed and shop chests that are. Shop chests that exist within a claim still require permissions to open the chest. 


## Command List
/shopshare add <player> - Adds a player to the user's shopshare list. If inside a claim, the target is added to that claim's local shopshare list for the user. If outside a claim, the target is added to the user's global shopshare list.

/shopshare remove <player> - Removes a player from the user's shopshare list. If inside a claim, the target is removed from that claim's local shopshare list for the user. If outside a claim, the target is removed from the user's global shopshare list.

/shopshare list - Lists all players in the user's shopshare list. If inside a claim, lists the user's local shopshare list. If outside a claim, lists the user's global shopshare list.

/shopshare help - Lists commands with basic usage guidelines.

/shopshare version - Tells the user the plugin version.
