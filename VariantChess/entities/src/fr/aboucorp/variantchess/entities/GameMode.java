package fr.aboucorp.variantchess.entities;

import fr.aboucorp.variantchess.entities.rules.AbstractRuleSet;

public class GameMode {
    private String name;
    private String description;
    private AbstractRuleSet ruleSet;

    public GameMode() {
    }

    public GameMode(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AbstractRuleSet getRuleSet() {
        return this.ruleSet;
    }

    public void setRuleSet(AbstractRuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }
}
