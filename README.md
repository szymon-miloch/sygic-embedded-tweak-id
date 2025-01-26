This repository reproduces the issue of not being able to set Sygic ID using Content Provider
havign embedded Sygic implementation.

The app launches embedded Sygic Fragment and shows button to set Sygic ID to a custom UUID.
After clicking on the button there's a text message above the button that will either
display correct Sygic ID or `Could not set Sygic ID` message if the ID was not set.

Please assist on this matter and let me know how to tweak Sygic ID in the embedded Sygic 
implementation.

Look for the error log "_Failed to tweak Sygic ID using content:_" to see exact issue that
happens when trying to set Sygic ID.