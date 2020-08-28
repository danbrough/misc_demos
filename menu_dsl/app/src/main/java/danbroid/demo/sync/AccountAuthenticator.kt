package danbroid.demo.sync

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import danbroid.demo.MainActivity

class AccountAuthenticator(val context: Context) : AbstractAccountAuthenticator(context) {

  override fun getAuthTokenLabel(authTokenType: String): String? {
    log.debug("getAuthTokenLabel(): $authTokenType")

    /*if (AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
      return AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
    else if (AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
      return AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
    else*/
    return "danbroid.demo.AuthTokenLabel";
  }

  override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle): Bundle? {
    log.debug("confirmCredentials() $account")
    return null
  }

  override fun updateCredentials(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle? {
    log.debug("updateCredentials()")
    return null
  }

  override fun getAuthToken(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle? {
    log.debug("getAuthToken() account:$account type: $authTokenType")
    /*
           // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        String authToken = am.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    authToken = sServerAuthenticate.userSignIn(account.name, password, authTokenType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(com.lodoss.authlib.Config.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(com.lodoss.authlib.Config.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(Config.ARG_ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
    bundle.putParcelable(AccountManager.KEY_INTENT, intent);
    return bundle;
     */
    return null
  }

  override fun hasFeatures(response: AccountAuthenticatorResponse?, account: Account?, features: Array<out String>?): Bundle? {
    log.debug("hasFeatures() $account features: $features")
    return bundleOf(AccountManager.KEY_BOOLEAN_RESULT to false)
  }

  override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle? {
    log.debug("editProperties() type:$accountType")
    return null
  }


  @Throws(NetworkErrorException::class)
  override fun addAccount(response: AccountAuthenticatorResponse?,
                          accountType: String?, authTokenType: String?, requiredFeatures: Array<out String>?, options:
                          Bundle?): Bundle {
    log.warn("addAcounnt() accountType:$accountType  authTokenType:$authTokenType requiredFeatures:$requiredFeatures options:$options")
    val bundle = Bundle()
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("ACCOUNT_TYPE", accountType)
    intent.putExtra("AUTH_TOKEN_TYPE", authTokenType)
    intent.putExtra("ADDING_NEW_ACCOUNT", true)
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
    bundle.putParcelable(AccountManager.KEY_INTENT, intent)
//    return bundle
    return bundleOf()
  }

  /* override fun addAccount(response: AccountAuthenticatorResponse?, accountType: String, authTokenType: String, requiredFeatures: Array<out String>?, options: Bundle?): Bundle? {
     log.debug("addAccount() accountType:$accountType authTokenType:$authTokenType")

     */
  /**
  final Intent intent = new Intent(mContext, CustomServerAuthenticatorSigninActivity.class);
  intent.putExtra(Config.ARG_ACCOUNT_TYPE, accountType);
  intent.putExtra(Config.ARG_AUTH_TYPE, authTokenType);
  intent.putExtra(Config.ARG_IS_ADDING_NEW_ACCOUNT, true);
  intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

  final Bundle bundle = new Bundle();
  bundle.putParcelable(AccountManager.KEY_INTENT, intent);
   *//*
    return null
  }*/
}

private val log = org.slf4j.LoggerFactory.getLogger(AccountAuthenticator::class.java)
