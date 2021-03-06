package com.blitline.image;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;

/**
 * Value object representing an Amazon S3 location. An instance of this class may be used for the initial source image, any of the
 * {@code src} parameters of the various functions, or a location to save to.
 *
 * @author Christopher Smith
 *
 */
public class S3Location implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Pattern fragment matching a valid new-style S3 bucket name (3 to 63 DNS characters).
	 */
	public static final String S3_BUCKET_SUBPATTERN = "([\\w\\d\\._-]{3,63})";

	/**
	 * Pattern matching a valid S3 bucket name ({@link #S3_BUCKET_SUBPATTERN} with start and end markers).
	 */
	public static final Pattern S3_BUCKET_PATTERN = Pattern.compile("\\A" + S3_BUCKET_SUBPATTERN + "\\z");

	/**
	 * Pattern matching a valid S3 URL of the form {@literal s3://bucket-name/key/name}.
	 */
	public static final Pattern S3_URL_PATTERN = Pattern.compile("\\As3://" + S3_BUCKET_SUBPATTERN + "/(.+)\\z");

	public final String bucket, key;

	public final Map<String, String> headers;

	public S3Location(String bucket, String key) {
	    this(bucket, key, Collections.<String,String>emptyMap());
	}

	public S3Location(String bucket, String key, Map<String, String> headers) {
        Validate.isTrue(S3_BUCKET_PATTERN.matcher(bucket).matches(), "bucket parameter '" + bucket
            + "' is not a valid S3 bucket name");
        this.bucket = bucket;
        this.key = key;
        this.headers = new HashMap<String, String>(headers);
	}

	public static S3Location of(String bucket, String key) {
		return new S3Location(bucket, key);
	}

	public static S3Location of(String s3Url) {
		Matcher m = S3_URL_PATTERN.matcher(s3Url);
		Validate.isTrue(m.matches(), "s3Url is not a valid S3 URL");
		return of(m.group(1), m.group(2));
	}

	public String getBucket() {
		return bucket;
	}

	public String getKey() {
		return key;
	}

	public Map<String, String> getHeaders() {
	    return Collections.unmodifiableMap(headers);
	}

	/**
	 * Adds the specified header when writing the object to S3. This header
	 * will be included when the object is served over HTTP.
	 *
	 * @param name the HTTP header's name
	 * @param value the HTTP header's value
	 * @return this {@code S3Location} object
	 */
	public S3Location withHeader(String name, String value) {
	    headers.put(name, value);
	    return this;
	}

	/**
	 * The name of the HTTP Cache-Control header.
	 */
	public static final String CACHE_CONTROL_HEADER_NAME = "Cache-Control";

	/**
	 * A value for the Cache-Control header that enables caching by proxies for one year.
	 * Suitable for static assets that will never change, such as uniquely-named images.
	 */
	public static final String CACHE_CONTROL_FOREVER_VALUE = "public, max-age=31536000";

    /**
     * Adds a canned "cache forever" Cache-Control header to the object in S3.
     *
     * @return this {@code S3Location} object
     */
	public S3Location withCacheControlHeader() {
	    return withHeader(CACHE_CONTROL_HEADER_NAME, CACHE_CONTROL_FOREVER_VALUE);
	}

	public String getName() {
		return "s3";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bucket == null) ? 0 : bucket.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof S3Location))
			return false;
		S3Location other = (S3Location) obj;
		if (bucket == null) {
			if (other.bucket != null)
				return false;
		} else if (!bucket.equals(other.bucket))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder("s3://").append(bucket).append('/').append(key).toString();
	}
}
