// префиксы товаров
prefixes = ["xxx", "yyy"]

// логин - пароль
credentials = [
        "user1": "pass1",
        "user2": "pass2",
]

// kkms id кассы -> [id: "(РН ККТ)_(№ ФН)", from: "(от ФД №)", to: "(до ФД №)", account: "(логин)"]
kkt.ranges = [
        "111111": [id: "0000111111111111_1111111111111111", from: 1, to: null, account: "user1"],
        "222222": [id: "0000222222222222_2222222222222222", from: 1, to: null, account: "user1"],
        "333333": [id: "0000333333333333_3333333333333333", from: 1, to: null, account: "user2"],
        "444444": [id: "0000444444444444_4444444444444444", from: 1, to: null, account: "user2"],
]
