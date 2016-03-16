# refreshable-token

The legacy implementation of `RefreshableToken`, `LegacyRefreshableToken`, is broken : you can find a use case where the token will be refreshed when it should not be.

**TODO**

1. write a test prooving that the legacy implementation has a flaw

2. fix the implementation of `LegacyRefreshableToken` without breaking the API

3. fix the implementation of `LegacyRefreshableToken`, this time you are allowed to break the API !

Solution are located in package solution, don't spoil !
