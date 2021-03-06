package com.blitline.image.functions;

import org.apache.commons.lang3.Validate;

public class ContrastStretchChannel extends AbstractFunction {

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return "contrast_stretch_channel";
	}

	public ContrastStretchChannel(int blackPoint) {
		Validate.isTrue(blackPoint >= 0);
		params.put("black_point", blackPoint);
	}

	public ContrastStretchChannel whitePoint(int whitePoint) {
		Validate.isTrue(whitePoint > (Integer) params.get("black_point"));
		params.put("white_point", whitePoint);
		return this;
	}
}
