package telegram.pms.com.example.demo.member;

// To convert full database entity into a cleaner API response in Postman
public record MemberResponse(
        Long id,
        String name,
        String telegramUsername
) {
    public static MemberResponse from(Member member) {
        String name = buildName(member.getFirstName(), member.getLastName());

        return new MemberResponse(
                member.getId(),
                name,
                member.getTelegramUsername()
        );
    }

    private static String buildName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return "";
        }

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }
}