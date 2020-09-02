fun ProjectVersions.getVersionName(
  projectVersion: Int = PROJECT_VERSION,
  betaVersion: Int = BETA_VERSION
) = "$MAJOR_VERSION.$MINOR_VERSION.${projectVersion}" +
    if (betaVersion > 0) "-beta%02d".format(betaVersion) else ""

fun ProjectVersions.getIncrementedVersionName() =
  if (BETA_VERSION < 0) getVersionName(PROJECT_VERSION + 1) else
    getVersionName(PROJECT_VERSION, BETA_VERSION + 1)


val ProjectVersions.VERSION_NAME: String
  get() = getVersionName()

