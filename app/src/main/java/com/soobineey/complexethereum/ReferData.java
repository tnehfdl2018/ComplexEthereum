package com.soobineey.complexethereum;

public class ReferData {

    // API 키
    private static final String API_KEY = "EJDCEPPATN3C28BQ8GXJPVABECXKFY21P7";

    // 각 코인별 조회 주소
    private static final String bnbAddress = "0xB8c77482e45F1F44dE1745F52C74426C631bDD52";
    private static final String mexAddress = "0x2ba6b1e4424e19816382d15937739959f7da5fd8";
    private static final String sxpAddress = "0x8ce9137d39326ad0cd6491fb5cc0cba0e089b6a9";
    private static final String linkAddress = "0x98c63b7b319dfbdf3d811530f2ab9dfe4983af9d";
    private static final String aoaAddress = "0x9ab165d795019b6d8b3e971dda91071421305e5a";

    // 공통 주소
    private static final String baseAddress = "https://api.etherscan.io/api?module=account&action=tokentx&address=";
    private static final String middleAddress = "&page=1&offset=10&sort=desc&apikey=";

    // 최종 조회할 주소
    public static final String bnbFullAddress = baseAddress + bnbAddress + middleAddress + API_KEY;
    public static final String mexFullAddress = baseAddress + mexAddress + middleAddress + API_KEY;
    public static final String sxpFullAddress = baseAddress + sxpAddress + middleAddress + API_KEY;
    public static final String linkFullAddress = baseAddress + linkAddress + middleAddress + API_KEY;
    public static final String aoaFullAddress = baseAddress + aoaAddress + middleAddress + API_KEY;
}
