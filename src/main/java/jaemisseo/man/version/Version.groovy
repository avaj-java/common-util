package jaemisseo.man.version

class Version {

    String nowVersion
    List<String> orderedVersions = Collections.EMPTY_LIST

    Version(String nowVersion){
        this.nowVersion = nowVersion
    }

    Version(String nowVersion, List<String> orderedVersions){
        this.nowVersion = nowVersion
        this.orderedVersions = orderedVersions
    }

    boolean equals(String checkingVersion){
        return this.nowVersion.equals(checkingVersion)
    }

    boolean lowerThan(String checkingVersion){
        int size = orderedVersions.size()
        int nowIndex = orderedVersions.indexOf(nowVersion)
        int checkingIndex = orderedVersions.indexOf(checkingVersion)
        return checkingIndex < nowIndex
    }

    static boolean equalsOrLowerThan(String checkingVersion){
        Version version = new Version('0.0.0')
        return version.equals(checkingVersion) || version.lowerThan(checkingVersion)
    }

    static boolean from(String checkingVersion){
        Version version = new Version('0.0.0')
        return version.equals(checkingVersion) || !version.lowerThan(checkingVersion)
    }


}
