package emulationstation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="image" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="rating" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="releasedate" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="developer" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="publisher" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="genre" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="players" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "path", "name", "desc", "image", "marquee", "video", "thumbnail", "rating", "releasedate", "developer", "publisher", "genre", "players", "favorite" })
public class GameListGame {

	@XmlElement(required = true)
	protected String path;
	@XmlElement(required = true)
	protected String name;
	@XmlElement
	protected String desc;
	@XmlElement(required = true)
	protected String image;
	@XmlElement(required = true)
	protected String rating;
	@XmlElement(required = true)
	protected String releasedate;
	@XmlElement(required = true)
	protected String developer;
	@XmlElement(required = true)
	protected String publisher;
	@XmlElement(required = true)
	protected String genre;
	@XmlElement(required = true)
	protected String players;

	@XmlElement
	protected String marquee;
	@XmlElement
	protected String video;
	@XmlElement
	protected String thumbnail;

	private boolean favorite;

	/**
	 * Gets the value of the path property.
	 * 
	 * @return possible String is {@link String }
	 * 
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the value of the path property.
	 * 
	 * @param value
	 *            allowed String is {@link String }
	 * 
	 */
	public void setPath(String value) {
		this.path = value;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible String is {@link String }
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed String is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Gets the value of the image property.
	 * 
	 * @return possible String is {@link String }
	 * 
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Sets the value of the image property.
	 * 
	 * @param value
	 *            allowed String is {@link String }
	 * 
	 */
	public void setImage(String value) {
		this.image = value;
	}

	/**
	 * Gets the value of the rating property.
	 * 
	 * @return possible String is {@link String }
	 * 
	 */
	public String getRating() {
		return rating;
	}

	/**
	 * Sets the value of the rating property.
	 * 
	 * @param value
	 *            allowed String is {@link String }
	 * 
	 */
	public void setRating(String value) {
		this.rating = value;
	}

	/**
	 * Gets the value of the releasedate property.
	 * 
	 * @return possible String is {@link String }
	 * 
	 */
	public String getReleasedate() {
		return releasedate;
	}

	/**
	 * Sets the value of the releasedate property.
	 * 
	 * @param value
	 *            allowed String is {@link String }
	 * 
	 */
	public void setReleasedate(String value) {
		this.releasedate = value;
	}

	/**
	 * Gets the value of the developer property.
	 * 
	 * @return possible String is {@link String }
	 * 
	 */
	public String getDeveloper() {
		return developer;
	}

	/**
	 * Sets the value of the developer property.
	 * 
	 * @param value
	 *            allowed String is {@link String }
	 * 
	 */
	public void setDeveloper(String value) {
		this.developer = value;
	}

	/**
	 * Gets the value of the publisher property.
	 * 
	 * @return possible String is {@link String }
	 * 
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * Sets the value of the publisher property.
	 * 
	 * @param value
	 *            allowed String is {@link String }
	 * 
	 */
	public void setPublisher(String value) {
		this.publisher = value;
	}

	/**
	 * Gets the value of the genre property.
	 * 
	 * @return possible String is {@link String }
	 * 
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * Sets the value of the genre property.
	 * 
	 * @param value
	 *            allowed String is {@link String }
	 * 
	 */
	public void setGenre(String value) {
		this.genre = value;
	}

	/**
	 * Gets the value of the players property.
	 * 
	 * @return possible String is {@link String }
	 * 
	 */
	public String getPlayers() {
		return players;
	}

	/**
	 * Sets the value of the players property.
	 * 
	 * @param value
	 *            allowed String is {@link String }
	 * 
	 */
	public void setPlayers(String value) {
		this.players = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMarquee() {
		return marquee;
	}

	public void setMarquee(String marquee) {
		this.marquee = marquee;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

}
