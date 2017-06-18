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

    GENERAL_LEVELUP,
    GENERAL_PETNAME,
    GENERAL_PETHEALTH,
    GENERAL_STATUS,

    ITEM_FOOD_NAME,
    ITEM_FOOD_TOOLTIP,

    ITEM_EGG_NAME,
    ITEM_EGG_LORE_REMAINING,
    ITEM_EGG_HATCH,

    ITEM_PET_DEFAULTNAME,

    ITEM_PET_LORE_LEVEL,
    ITEM_PET_LORE_TYPE,
    ITEM_PET_LORE_EXP,
    ITEM_PET_LORE_MAXLEVEL,
    ITEM_PET_LORE_AGE,
    ITEM_PET_LORE_ADULT,
    ITEM_PET_LORE_BABY,

    ITEM_PET_LORE_STATUS,
    ITEM_PET_LORE_SPAWNED,
    ITEM_PET_LORE_READY,
    ITEM_PET_LORE_DEAD, GENERAL_NAMEONCE, GENERAL_FEEDCOOLDOWN;


    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String get(String... replacements) {
        String tmp = message;
        for (int i = 0; i < replacements.length; i++)
            tmp = tmp.replace("{" + i + "}", replacements[i]);

        return tmp;
    }

}
