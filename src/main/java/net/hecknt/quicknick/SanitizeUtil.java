package net.hecknt.quicknick;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SanitizeUtil {
    public static MutableComponent sanitize(@Nullable Component input) {
        try {
            MutableComponent output;
            if(input.plainCopy().getString().equals("")) {
                 output = input.plainCopy();
            }else{
                output = Component.literal("");
            }
            Style inputStyle = sanitize_style(input.getStyle());
            List<Component> comps = input.toFlatList();
            for(Component c : comps){
                if(!sanitize_content(c)){
                    continue;
                }
                MutableComponent mut = c.copy();
                Style cStyle = sanitize_style(c.getStyle());
                mut.setStyle(cStyle);
                output.append(mut);
            }
            return output.setStyle(inputStyle);
        }catch(Exception e){
            return (MutableComponent) input;
        }
    }

    private static boolean sanitize_content(Component c){
        if(c.getContents() instanceof ScoreContents){
            return Config.removeScore;
        }
        if(c.getContents() instanceof KeybindContents){
            return Config.removeKeybind;
        }
        if(c.getContents() instanceof SelectorContents){
            return Config.removeSelector;
        }
        if(c.getContents() instanceof NbtContents){
            return Config.removeNbt;
        }
        return true;
    }

    private static Style sanitize_style(Style inputStyle){
        if (Config.removeClickEvent) {
            inputStyle = inputStyle.withClickEvent(null);
        }
        if (Config.removeInsertions) {
            inputStyle = inputStyle.withInsertion(null);
        }
        if (Config.removeHoverEvent) {
            inputStyle = inputStyle.withHoverEvent(null);
        }
        return inputStyle;
    }
}
