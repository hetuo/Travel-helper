package cs601.hotelapp;


/**
 * Class Review - a data structure that stores information 
 * about review of hotels. 
 * @author @TuoHe
 */
public class Review implements Comparable<Review>{
	

	private String reviewId;
	private String hotelId;
	private String title;
	private String text;
	private String username;
	private String date;
	private boolean recom;
	private int rating;


	/** A default constructor for class Review
	 * @param reviewId
	 * 		The id of review
	 * @param hotelId
	 * 		The id of hotel about which this review talks 
	 * @param title
	 * 		The title of review
	 * @param text
	 * 		The content of review
	 * @param username
	 * 		The user name of author who written this review
	 * @param date
	 * 		The date when this review been written
	 * @param recom
	 * 		Is this hotel been recommend
	 * @param rating
	 * 		The rating of review
	 * */
	public Review(String reviewId, String hotelId, String title, String text, String username, String date, 
			boolean recom, int rating)
	{
		this.reviewId = reviewId;
		this.hotelId = hotelId;
		this.title = title;
		this.text = text;
		this.username = username;
		this.date = date;
		this.recom = recom;
		this.rating = rating;
		
	}
	
	/** A method to get if recommend
	 * @return the result if is recommend */
	public boolean getRecom()
	{
		return recom;
	}
	
	/** A method to set if recommend
	 * @param recom
	 * 		The result if is recommend that want to set to this review */
	public void setRecom(boolean recom)
	{
		this.recom = recom;
	}
	
	/** A method to get rating
	 * @return the rating */
	public int getRating()
	{
		return rating;
	}
	
	/** A method to set rating
	 * @param rating
	 * 		The rating that want to set to this review */
	public void setRating(int rating)
	{
		this.rating = rating;
	}
	
	/** A method to get review id
	 * @return the review id */
	public String getReviewId()
	{
		return reviewId;
	}
	
	/** A method to set review id
	 * @param rId
	 * 		The review id if is recommend that want to set to this review */
	public void setReviewId(String rId)
	{
		reviewId = rId;
	}

	/** A method to get hotel id
	 * @return the hotel id */
	public String getHotelId()
	{
		return hotelId;
	}
	
	/** A method to set hotel id 
	 * @param hId
	 * 		The hotel id that want to set to this review */
	public void setHotelId(String hId)
	{
		hotelId = hId;
	}
	
	/** A method to get review's title
	 * @return the review's title */
	public String getReviewTitle()
	{
		return title;
	}
	
	/** A method to set review's title
	 * @param reviewTitle
	 * 		The review's title that want to set to this review */
	public void setReviewTitle(String reviewTitle)
	{
		title = reviewTitle;
	}
	
	/** A method to get review's content
	 * @return the review's content */
	public String getReviewText()
	{
		return text;
	}
	
	/** A method to set review's content
	 * @param reviewText
	 * 		The review's content that want to set to this review */
	public void setReviewText(String reviewText)
	{
		text = reviewText;
	}
	
	/** A method to get review's date
	 * @return review's date */
	public String getReviewDate()
	{
		return date;
	}
	
	/** A method to set review's date
	 * @param reviewDate
	 * 		The review's date that want to set to this review */
	public void setReviewDate(String reviewDate)
	{
		date = reviewDate;
	}
	
	/** A method to get review's user name
	 * @return the review's user name */
	public String getReviewUsername()
	{
		return username;
	}
	
	/** A method to set review's user name
	 * @param reviewUsername
	 * 		The review's user name that want to set to this review */
	public void setReviewUsername(String reviewUsername)
	{
		username = reviewUsername;
	}
	

	/**

	 * Compare reviews based on (a)date((a review is "less" than another review, if it was submitted earlier)
	 * 	(b) if the dates are the same, based on user nicknames (in alphabetical order). 
	 * 	
	 * @param R - Another review which need to be compared with this review.
     *
	 * @return Instance of the following:
	 * 	0 - Two reviews are "equally",
	 * 	1 - This review is "greater" than another one,
	 * 	-1 - This review is "lesser" than another one.
	 */
	@Override
	public int compareTo(Review R)
	{
		if (0 == this.date.compareTo(R.getReviewDate()))
		{
			if (username.compareTo(R.getReviewUsername()) < 0)
			{
				return -1;
			}
			else if (username.compareTo(R.getReviewUsername()) > 0)
			{
				return 1;
			}
			else
			{				
				return this.reviewId.compareTo(R.getReviewId());					
			}
		}
		else if (this.date.compareTo(R.getReviewDate()) < 0)	
			return -1;
		else
			return 1;
	}

}
