package com.maksimowiczm.foodyou.feature.usda.model

enum class Nutrient(val number: String) {
    CALORIES("957"),
    CALORIES_ALTERNATIVE("208"),

    PROTEIN("203"),
    CARBOHYDRATE("205"),
    FAT("204"),
    SATURATED_FAT("606"),
    MONOUNSATURATED_FAT("645"),
    POLYUNSATURATED_FAT("646"),
    TRANS_FAT("605"),
    // OMEGA_3(""),
    // OMEGA_6(""),

    SUGARS("269"),
    ADDED_SUGARS("539"),
    FIBER("291"),
    // SOLUBLE_FIBER(""),
    // INSOLUBLE_FIBER(""),

    CHOLESTEROL("601"),
    CAFFEINE("262"),

    VITAMIN_A("320"), // 318 ?
    VITAMIN_B1("404"),
    VITAMIN_B2("405"),
    VITAMIN_B3("406"),
    VITAMIN_B5("410"),
    VITAMIN_B6("415"),

    VITAMIN_B7("416"),
    VITAMIN_B9("417"),
    VITAMIN_B12("418"),
    VITAMIN_C("401"),
    VITAMIN_D("324"),
    VITAMIN_E("323"),
    VITAMIN_K("430"),

    MANGANESE("315"),
    MAGNESIUM("304"),
    POTASSIUM("306"),
    CALCIUM("301"),
    COPPER("312"),
    ZINC("309"),
    SODIUM("307"),
    IRON("303"),
    PHOSPHORUS("305"),
    SELENIUM("317")
    // IODINE(""),
    // CHROMIUM("")
}
