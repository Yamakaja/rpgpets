package me.yamakaja.rpgpets.api.config;

/**
 * Created by Yamakaja on 10.06.17.
 */
public enum ConfigMessages {

    COMMAND_NOPERM,
    COMMAND_SYNTAX,

    COMMAND_HELP_HINT,
    COMMAND_HELP_DESCRIPTION,
    COMMAND_HELP_SUBCOMMANDS,
    COMMAND_HELP_SUBCOMMAND,

    COMMAND_GIVE_DESCRIPTION,
    COMMAND_GIVE_SYNTAX,
    COMMAND_GIVE_TARGETNOTFOUND,
    COMMAND_GIVE_UNKNOWN,
    COMMAND_GIVE_SUCCESS,


    GENERAL_LEVELUP;

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String get(String... replacements) {
        String tmp = message;
        for (int i = 0; i < replacements.length; i++)
            tmp = tmp.replace("{" + i + "}", replacements[0]);

        return tmp;
    }

}
