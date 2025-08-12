package com.guhao.perfect_dodge;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
	@Override public void connect(){
		Mixins.addConfiguration("perfect_dodge.mixins.json");
	}
}
