package autosimmune.env;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;

public class AutoSimmuneBuilder implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {
		Global global = new Global();
		context.addSubContext(global);
		context.add(global);
		global.build(context);
		return context;
	}

}
