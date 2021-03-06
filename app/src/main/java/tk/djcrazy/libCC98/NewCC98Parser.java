package tk.djcrazy.libCC98;

import android.text.Html;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.StringEscapeUtils;

import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tk.djcrazy.libCC98.data.BoardEntity;
import tk.djcrazy.libCC98.data.BoardStatus;
import tk.djcrazy.libCC98.data.Gender;
import tk.djcrazy.libCC98.data.HotTopicEntity;
import tk.djcrazy.libCC98.data.InboxInfo;
import tk.djcrazy.libCC98.data.LoginType;
import tk.djcrazy.libCC98.data.PmInfo;
import tk.djcrazy.libCC98.data.PostContentEntity;
import tk.djcrazy.libCC98.data.PostEntity;
import tk.djcrazy.libCC98.data.SearchResultEntity;
import tk.djcrazy.libCC98.data.UserProfileEntity;
import tk.djcrazy.libCC98.data.UserStatue;
import tk.djcrazy.libCC98.data.UserStatueEntity;
import tk.djcrazy.libCC98.exception.NoUserFoundException;
import tk.djcrazy.libCC98.exception.ParseContentException;
import tk.djcrazy.libCC98.util.DateFormatUtil;
import tk.djcrazy.libCC98.util.RegexUtil;
import tk.djcrazy.libCC98.util.StringUtil;

/*
import static tk.djcrazy.libCC98.CC98ParseRepository.HOT_TOPIC_BOARD_ID_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.HOT_TOPIC_BOARD_NAME_WITH_AUTHOR_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.HOT_TOPIC_CLICK_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.HOT_TOPIC_ID_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.HOT_TOPIC_NAME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.HOT_TOPIC_POST_TIME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.HOT_TOPIC_WRAPPER;
import static tk.djcrazy.libCC98.CC98ParseRepository.NEW_TOPIC_AUTHOR_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.NEW_TOPIC_BOARD_ID;
import static tk.djcrazy.libCC98.CC98ParseRepository.NEW_TOPIC_FACE_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.NEW_TOPIC_ID_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.NEW_TOPIC_TIME;
import static tk.djcrazy.libCC98.CC98ParseRepository.NEW_TOPIC_TITLE_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.NEW_TOPIC_TOTAL_POST;
import static tk.djcrazy.libCC98.CC98ParseRepository.NEW_TOPIC_WRAPPER_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_CONTENT_GENDER_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_CONTENT_INFO_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_CONTENT_POST_CONTENT_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_CONTENT_POST_FACE_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_CONTENT_POST_TIME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_CONTENT_POST_TITLE_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_CONTENT_USERNAME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_CONTENT_USER_AVATAR_LINK_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_CONTENT_WHOLE_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_LIST_LAST_REPLY_AUTHOR_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_LIST_LAST_REPLY_TIME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_LIST_POST_AUTHOR_NAME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_LIST_POST_BOARD_ID_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_LIST_POST_ENTITY_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_LIST_POST_ID_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_LIST_POST_NAME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_LIST_POST_TYPE_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.POST_LIST_REPLY_NUM_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.P_BOARD_ID_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.P_BOARD_LAST_REPLY_AUTHOR_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.P_BOARD_LAST_REPLY_TIME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.P_BOARD_LAST_REPLY_TOPIC_ID_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.P_BOARD_LAST_REPLY_TOPIC_NAME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.P_BOARD_NAME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.P_BOARD_POST_NUMBER_TODAY;
import static tk.djcrazy.libCC98.CC98ParseRepository.P_BOARD_SINGLE_BOARD_WRAPPER_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.TODAY_BOARD_ENTITY_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.TODAY_BOARD_ID_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.TODAY_BOARD_NAME_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.TODAY_BOARD_TOPIC_NUM_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.TODAY_POST_NUMBER_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.USER_PROFILE_AVATAR_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.USER_PROFILE_GENERAL_PROFILE_REGEX;
import static tk.djcrazy.libCC98.CC98ParseRepository.USER_PROFILE_ONLINE_INFO_REGEX;
*/
import static tk.djcrazy.libCC98.CC98ParseRepository.*;
import static tk.djcrazy.libCC98.util.DateFormatUtil.convertStringToDateInPostContent;
import static tk.djcrazy.libCC98.util.RegexUtil.getMatchedString;
import static tk.djcrazy.libCC98.util.RegexUtil.getMatchedStringList;
import static tk.djcrazy.libCC98.util.StringUtil.filterHtmlDecode;

@Singleton
public class NewCC98Parser {
	private static final String TAG = "CC98ParserImpl";


    @Inject
    private ICC98UrlManager cc98UrlManager;
	/**
	 * Parse the HTML, and put posts and their poster id in a list of
	 * NameValuePair
	 *
	 * @param html
	 *            String (must not be null)
	 * @return A list of the pair of poster id and their posts
	 * @throws tk.djcrazy.libCC98.exception.ParseContentException
	 * @throws java.text.ParseException
	 */
	public List<PostContentEntity> parsePostContentList(String html) throws ParseContentException,
			java.text.ParseException {
		List<PostContentEntity> list = new ArrayList<PostContentEntity>();
		// get some information of the topic
		List<String> postInfoList = getMatchedStringList(POST_CONTENT_INFO_REGEX, html, 3);
		PostContentEntity postInfoEntity = new PostContentEntity();
		postInfoEntity.setPostTopic(filterHtmlDecode(postInfoList.get(0)));
		postInfoEntity.setBoardName(Html.fromHtml(postInfoList.get(1)).toString());
		postInfoEntity.setTotalPage((int) Math.ceil(Integer.parseInt(postInfoList.get(2)) / 10.0));
		list.add(postInfoEntity);

		// get each reply info
		List<String> contentHtml = getMatchedStringList(POST_CONTENT_WHOLE_REGEX, html, -1);
		for (String reply : contentHtml) {
			PostContentEntity entity = new PostContentEntity();
			try {
				entity.setUserName(Html.fromHtml(getMatchedString(POST_CONTENT_USERNAME_REGEX, reply))
					.toString());


                //solve markdown parse problems
				try{
                    String _notused=getMatchedString(POST_CONTENT_ITEM_MARKDOWN_SYNTAX_CHECK,reply);
                    //no throw,then just apply markdown;
                    entity.setSupportMarkDownSyntax(true);
                    String tmpcoveredContent=getMatchedString(POST_CONTENT_ITEM_MARKDOWN_REGEX, reply).replaceAll("\\s*</textarea>\\s*</div>","</textarea></div>");
                    //convert html-encrypted to normal code
                    String coveredContent = StringEscapeUtils.unescapeHtml4(tmpcoveredContent);
                    entity.setPostContent(coveredContent);

                }catch (ParseContentException e){
                    //no markdown;
                    entity.setSupportMarkDownSyntax(false);
                    entity.setPostContent(getMatchedString(POST_CONTENT_POST_CONTENT_REGEX, reply));

                }




				entity.setPostTitle(getMatchedString(POST_CONTENT_POST_TITLE_REGEX, reply));
 				entity.setPostFace(getMatchedString(POST_CONTENT_POST_FACE_REGEX, reply));
 				entity.setPostTime(convertStringToDateInPostContent(getMatchedString(
					POST_CONTENT_POST_TIME_REGEX, reply)));
			} catch (Exception e) {
				e.printStackTrace();
 			}
			try {
				String avatarLink = getMatchedString(POST_CONTENT_USER_AVATAR_LINK_REGEX, reply);
				if (!
                        (avatarLink.contains("http://")||avatarLink.contains("https://"))
                        ) {
					avatarLink = cc98UrlManager.getClientUrl() + avatarLink;
				}
				entity.setUserAvatarLink(avatarLink);

			} catch (ParseContentException e) {
				entity.setUserAvatarLink("file:///android_asset/pic/no_avatar.jpg");
			}
			{
				String sex = getMatchedString(POST_CONTENT_GENDER_REGEX, reply);
				if (!sex.toLowerCase().contains("female")) {
                    // String sex may contain "ofMale","ofFeMale","Male","Female";
                    // directly use "".equal() is not safe;
                    //sex.contains("Female");
					entity.setGender(Gender.MALE);
				} else {
					entity.setGender(Gender.FEMALE);
				}

			}
			list.add(entity);
		}
		return list;
	}

	/**
	 * Parse the HTML to obtain posts names and their URL.
	 *
	 * @param html
	 * @return
	 * @throws tk.djcrazy.libCC98.exception.ParseContentException
	 * @throws java.text.ParseException
	 */
    public List<PostEntity> parsePostList(String html) throws ParseContentException,
			java.text.ParseException {

		List<PostEntity> list = new ArrayList<PostEntity>();
		List<String> contentList = getMatchedStringList(POST_LIST_POST_ENTITY_REGEX, html, -1);
		for (String post : contentList) {
			PostEntity entity = new PostEntity();
			entity.setPostName(Html.fromHtml(getMatchedString(POST_LIST_POST_NAME_REGEX, post))
					.toString());
 			entity.setPostType(getMatchedString(POST_LIST_POST_TYPE_REGEX, post));
			entity.setReplyNumber(getMatchedString(POST_LIST_REPLY_NUM_REGEX, post).replaceAll(
					"<.*?>", ""));
			entity.setPostAuthorName(Html.fromHtml(
					getMatchedString(POST_LIST_POST_AUTHOR_NAME_REGEX, post)).toString());
			entity.setLastReplyAuthor(Html.fromHtml(
					getMatchedString(POST_LIST_LAST_REPLY_AUTHOR_REGEX, post)).toString());
			entity.setLastReplyTime(DateFormatUtil.convertStringToDateInPostList(getMatchedString(
					POST_LIST_LAST_REPLY_TIME_REGEX, post)));
			entity.setPostId(getMatchedString(POST_LIST_POST_ID_REGEX, post));
			entity.setBoardId(getMatchedString(POST_LIST_POST_BOARD_ID_REGEX, post));
			list.add(entity);
		}
		return list;
	}

	/**
	 *
	 * @param html
	 * @return
	 * @throws tk.djcrazy.libCC98.exception.ParseContentException
	 * @throws java.text.ParseException
	 */
    public List<BoardEntity> parsePersonalBoardList(String html) throws ParseContentException,
			java.text.ParseException {

		List<BoardEntity> nList = new ArrayList<BoardEntity>();
		String boardinfo = html;
		List<String> board = getMatchedStringList(P_BOARD_SINGLE_BOARD_WRAPPER_REGEX, boardinfo, 0);
		for (String string : board) {
            //for each board;
			BoardEntity entity = new BoardEntity();
            String currentBoardID=getMatchedString(P_BOARD_ID_REGEX, string);
			entity.setBoardID(currentBoardID);
			/*
			try {
				entity.setChildBoardNumber(Integer.parseInt(getMatchedString(CC98ParseRepository.P_IS_PARENT_BOARD_REGEX, string)));
			} catch (ParseContentException e) {
				entity.setChildBoardNumber(0);
			}
			*/
            entity.setChildBoardNumber(0);

			try {
				entity.setPostNumberToday(Integer.parseInt(getMatchedString(
						P_BOARD_POST_NUMBER_TODAY, string)));
				entity.setBoardName(Html.fromHtml(getMatchedString(P_BOARD_NAME_REGEX, string))
						.toString());
				entity.setLastReplyBoardId(getMatchedString(CC98ParseRepository.P_BOARD_LAST_REPLY_BOARDID_REGEX, string));
				entity.setLastReplyAuthor(Html.fromHtml(
						getMatchedString(P_BOARD_LAST_REPLY_AUTHOR_REGEX, string)).toString());
				entity.setLastReplyTime(DateFormatUtil.convertStrToDateInPBoard(getMatchedString(
                        P_BOARD_LAST_REPLY_TIME_REGEX, string)));
				entity.setLastReplyTopicID(getMatchedString(P_BOARD_LAST_REPLY_TOPIC_ID_REGEX,
						string));
				entity.setLastReplyTopicName(Html.fromHtml(
						getMatchedString(P_BOARD_LAST_REPLY_TOPIC_NAME_REGEX, string)).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			nList.add(entity);
		}
		return nList;
	}

	/**
	 *
	 * @param html
	 * @return
	 * @throws tk.djcrazy.libCC98.exception.ParseContentException
	 * @throws java.text.ParseException
	 */
    public List<BoardEntity> parseBoardList(String html) throws ParseContentException,
			java.text.ParseException {

		List<BoardEntity> nList = new ArrayList<BoardEntity>();
		String boardinfo = html;
		List<String> board = getMatchedStringList(CC98ParseRepository.LIST_BOARD_SINGLE_BOARD_WRAPPER_REGEX, boardinfo, 0);
		for (String string : board) {
			BoardEntity entity = new BoardEntity();
			entity.setBoardID(getMatchedString(CC98ParseRepository.LIST_BOARD_ID_REGEX, string));
			try {
				entity.setChildBoardNumber(Integer.parseInt(getMatchedString(CC98ParseRepository.LIST_IS_PARENT_BOARD_REGEX, string)));
			} catch (ParseContentException e) {
				entity.setChildBoardNumber(0);
			}
			try {
				entity.setBoardName(Html.fromHtml(getMatchedString(CC98ParseRepository.LIST_BOARD_NAME_REGEX, string).replace("<.*?>", ""))
						.toString());
				entity.setLastReplyAuthor(Html.fromHtml(
						getMatchedString(CC98ParseRepository.LIST_BOARD_LAST_REPLY_AUTHOR_REGEX, string)).toString());
				entity.setLastReplyBoardId(getMatchedString(CC98ParseRepository.LIST_BOARD_LAST_REPLY_BOARDID_REGEX, string));
				entity.setLastReplyTime(DateFormatUtil.convertStrToDateInPBoard(getMatchedString(
						CC98ParseRepository.LIST_BOARD_LAST_REPLY_TIME_REGEX, string)));
				entity.setLastReplyTopicID(getMatchedString(CC98ParseRepository.LIST_BOARD_LAST_REPLY_TOPIC_ID_REGEX,
						string));
				entity.setLastReplyTopicName(Html.fromHtml(
						getMatchedString(CC98ParseRepository.LIST_BOARD_LAST_REPLY_TOPIC_NAME_REGEX, string)).toString());
				entity.setPostNumberToday(Integer.parseInt(getMatchedString(
						CC98ParseRepository.LIST_BOARD_POST_NUMBER_TODAY, string)));
			} catch (Exception e) {
                Logger.t(NewCC98Parser.class.getSimpleName()).i("parseBoardList failed", e);
			}
			nList.add(entity);
		}
		return nList;
	}


    public String parseUserAvatar(String html, LoginType  loginType, String proxyHost) {
        try {
            String url = getMatchedString(USER_PROFILE_AVATAR_REGEX, html);
            if (!url.startsWith("http") && !url.startsWith("ftp")) {
                url = cc98UrlManager.getClientUrl(loginType, proxyHost) + url;
            }
            return url;
        } catch (Exception e) {
              return cc98UrlManager.getClientUrl(loginType, proxyHost) + "PresetFace/male_1.gif";
        }
    }

	/**
	 * @author DJ
	 * @param html
	 * @return
	 * @throws tk.djcrazy.libCC98.exception.ParseContentException
	 */
    public UserProfileEntity parseUserProfile(String html) throws ParseContentException {
		UserProfileEntity entity = new UserProfileEntity();
		// avatar link
		{
			String url = getMatchedString(USER_PROFILE_AVATAR_REGEX, html);
			if (!url.startsWith("http") && !url.startsWith("ftp")) {
				url = cc98UrlManager.getClientUrl() + url;
			}
			entity.setUserAvatarLink(url);
		}
		// general profile
		{
			String info = getMatchedString(USER_PROFILE_GENERAL_PROFILE_REGEX, html);
			String[] details = info.split("<br>");
			entity.setUserNickName(details[0]);
			entity.setUserLevel(details[1]);
			entity.setUserGroup(details[2]);
			entity.setGoodPosts(details[3]);
			entity.setTotalPosts(details[4]);
			entity.setUserPrestige(details[5]);
			entity.setRegisterTime(details[6]);
			entity.setLoginTimes(details[7]);
			entity.setDeletedPosts(details[8]);
			entity.setDeletedRatio(details[9]);
			entity.setLastLoginTime(details[10]);
		}
		// personal profile
		try {
			String info = getMatchedString(USER_PROFILE_PERSON_PROFILE_REGEX, html);
			String[] details = info.split("<br>");

			details[1] = details[1].replaceAll("<.*?>", "");
			details[2] = details[2].replaceAll("<.*?>", "");
			details[3] = details[3].replaceAll("<.*?>", "");
			details[4] = details[4].replaceAll("<.*?>", "");
			details[5] = details[5].replaceAll("&nbsp;", " ");
			details[5] = details[5].replaceAll("<.*?>", "");
			details[6] = details[6].replaceAll("<.*?>", "");

			Pattern pattern = Pattern.compile("(?<=alt=).*?座", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(details[2]);
			if (matcher.find()) {
				details[2] = matcher.group();
			}
			entity.setUserGender(details[0]);
			entity.setUserBirthday(details[1]);
			entity.setUserConstellation(details[2]);
			entity.setUserEmail(details[3]);
			entity.setUserQQ(details[4]);
			entity.setUserMSN(details[5]);
			entity.setUserPage(details[6]);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// bbs master info
		{
			String string = getMatchedString(USER_PROFILE_AVATAR_REGEX, html);
			string = string.replaceAll("\t|\n|\r|<br>|&nbsp;|<.*?>| ", "");
			entity.setBbsMasterInfo(string);
		}
		// online status
		entity.setOnlineTime(getMatchedString(USER_PROFILE_ONLINE_INFO_REGEX, html));
		return entity;
	}

    public List<HotTopicEntity> parseHotTopicList(String page) throws ParseContentException {
		List<HotTopicEntity> list = new ArrayList<HotTopicEntity>();
		List<String> topicList = getMatchedStringList(HOT_TOPIC_WRAPPER, page, -1);
		for (String topic : topicList) {
			HotTopicEntity entity = new HotTopicEntity();
			entity.setTopicName(filterHtmlDecode(getMatchedString(HOT_TOPIC_NAME_REGEX, topic)));
			entity.setPostId(getMatchedString(HOT_TOPIC_ID_REGEX, topic));
			entity.setPostTime(getMatchedString(HOT_TOPIC_POST_TIME_REGEX, topic));
			entity.setBoardId(getMatchedString(HOT_TOPIC_BOARD_ID_REGEX, topic));
			// click number
			{
				List<String> numList = getMatchedStringList(HOT_TOPIC_CLICK_REGEX, topic, 3);
				entity.setFocusNumber(Integer.parseInt(numList.get(0)));
				entity.setReplyNumber(Integer.parseInt(numList.get(1)));
				entity.setClickNumber(Integer.parseInt(numList.get(2)));
			}
			// board name, author
			{
				List<String> bList = getMatchedStringList(HOT_TOPIC_BOARD_NAME_WITH_AUTHOR_REGEX,
						topic, -1);
				entity.setBoardName(bList.get(0));
				if (bList.size() < 2) {
					entity.setPostAuthor("匿名");
				} else {
					entity.setPostAuthor(bList.get(1));
				}
			}
			list.add(entity);
		}
		return list;
	}

	/**
	 * Store information of the msgs in a list
	 * 
	 * @author zsy
	 * @param html
	 *            The html of the inbox page
	 * @return A list of PmInfo
	 */
    public InboxInfo parsePmList(String html) {
		InboxInfo info = new InboxInfo();
        	String regexString = "";
        	if (html.indexOf("已发送的消息") > 0) {
            	regexString = "(?<=<img src=pic/m_)\\w+(?=\\.gif>)|(?<=target=_blank>)[^:]+(?=</a>)|(?<=\\s>).*?(?=</a></td>)|" +
                    	"(?<=<a href=\"messanger.asp\\?action=(read|outread)&id=)\\d+?(?=&sender)|(?<=target=_blank>).*?(?=</a></td>)";
        	}
        	else {
            	regexString = "(?<=<img src=pic/m_)\\w+(?=\\.gif>)|(?<=target=\"_blank\">)[^:]+(?=</a>)|(?<=\\s>).*?(?=</a></td>)|" +
                    	"(?<=<a href=\"messanger.asp\\?action=(read|outread)&id=)\\d+?(?=&sender)|(?<=target=_blank>).*?(?=</a></td>)|" +
                    	"(?<=gray;\">).+(?=</span>)";
        	}
        	html = html.substring(html.indexOf("新</span>"));
		List<PmInfo> pmList = new ArrayList<PmInfo>();
		Matcher m1 = Pattern.compile(regexString).matcher(html);
		getInboxList(pmList, m1);
		// Get total page number
		Pattern p2 = Pattern.compile("(?<=/<b>)\\d+(?=</b>页)");
		Matcher m2 = p2.matcher(html);
		if (m2.find()) {
			// Get the total page number of the pm inbox.
            info.setTotalInPage(Integer.parseInt(m2.group()));
		}
		// Get total pm count
		Pattern p3 = Pattern.compile("(?<=总数<b>)\\d+(?=</b></td>)");
		Matcher m3 = p3.matcher(html);
		if (m3.find()) {
            info.setTotalPmIn(Integer.parseInt(m3.group()));
		}
        info.setPmInfos(pmList);
 		return info;
	}

	/**
	 * Get a list of PmInfo.
	 * 
	 * @author zsy
	 * 
	 * @param pmList
	 * @param m1
	 */
    private void getInboxList(List<PmInfo> pmList, Matcher m1) {
		while (m1.find()) {
			String isNewString = m1.group();
			boolean isNew = isNewString.equals("olds") || isNewString.equals("issend_1") ? false
					: true;
			m1.find();
			String sender = m1.group();
			m1.find();
			String topic = m1.group();
			m1.find();
			int pmId = Integer.parseInt(m1.group());
			m1.find();
			String time = m1.group();
			pmList.add(new PmInfo.Builder(pmId).fromWho(sender).topicTitle(topic).sendTime(time)
					.newTopic(isNew).userAvatar("").build());
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see tk.djcrazy.libCC98.ICC98Parser#parseQueryResult(java.lang.String)
	 */
	public List<SearchResultEntity> parseQueryResult(String html) throws ParseContentException,
			java.text.ParseException {
		List<SearchResultEntity> list = new ArrayList<SearchResultEntity>();
		String totalPost;
		try {
			totalPost = getMatchedString(NEW_TOPIC_TOTAL_POST, html);
		} catch (Exception e) {
			e.printStackTrace();
			totalPost = "0";
			return list;
		}
		List<String> entityList = getMatchedStringList(NEW_TOPIC_WRAPPER_REGEX, html, -1);
		for (int i = 0; i < entityList.size(); i++) {
			String string = entityList.get(i);
			SearchResultEntity entity = new SearchResultEntity();
			entity.setTitle(StringUtil.filterHtmlDecode(getMatchedString(NEW_TOPIC_TITLE_REGEX,
					string)));
			try {
				entity.setAuthorName(getMatchedString(NEW_TOPIC_AUTHOR_REGEX, string));
			} catch (Exception e) {
				entity.setAuthorName("匿名");
			}
			entity.setBoardId(getMatchedString(NEW_TOPIC_BOARD_ID, string));
			entity.setFaceId(getMatchedString(NEW_TOPIC_FACE_REGEX, string));
			entity.setPostTime(DateFormatUtil.convertStringToDateInQueryResult(getMatchedString(
					NEW_TOPIC_TIME, string).replaceAll("&nbsp;", " ").replaceAll("\n|\t|\r", " ")
					.trim()));
			entity.setTotalResult(totalPost);
			entity.setPostId(getMatchedString(NEW_TOPIC_ID_REGEX, string));
			list.add(entity);
		}
		return list;
	}

    public List<UserStatueEntity> parseUserFriendList(String html) throws
			ParseException, IOException, ParseContentException {
		if (html == null) {
			throw new IllegalArgumentException("Null pointer!");
		}
		List<UserStatueEntity> list = new ArrayList<UserStatueEntity>();
		Pattern userRegexPattern = Pattern.compile("&nbsp;<a href=dispuser\\.asp\\?name=.*?<br>",
				Pattern.DOTALL);
		Pattern userNamePattern = Pattern.compile("(?<= >).*?(?=</a>)", Pattern.DOTALL);
		Pattern userStatuePattern = Pattern.compile("(?<=\\[).*?(?=\\])", Pattern.DOTALL);
		Matcher matcher = userRegexPattern.matcher(html);
		while (matcher.find()) {
			String mString = matcher.group();
			UserStatueEntity mEntity = new UserStatueEntity();
			Matcher mMatcher = userNamePattern.matcher(mString);
			if (mMatcher.find()) {
				mEntity.setUserName(mMatcher.group());
			}
			mMatcher = userStatuePattern.matcher(mString);
			if (mMatcher.find()) {
				String string = mMatcher.group().replaceAll("<.*?>", "");
				if (string.contains("离线")) {
					mEntity.setStatue(UserStatue.OFF_LINE);
				} else {
					mEntity.setStatue(UserStatue.ON_LINE);
					mEntity.setOnlineTime(string);
				}
			}
 			list.add(mEntity);
		}
		return list;
	}

    public List<BoardStatus> parseTodayBoardList(String content) throws ParseContentException {
		int postNum = Integer.parseInt(getMatchedString(TODAY_POST_NUMBER_REGEX, content));
		List<BoardStatus> list = new ArrayList<BoardStatus>();
		List<String> contentList = getMatchedStringList(TODAY_BOARD_ENTITY_REGEX, content, -1);
		for (int i = 0; i < contentList.size(); i++) {
			String string = contentList.get(i);
			BoardStatus status = new BoardStatus();
			status.setBoardId(getMatchedString(TODAY_BOARD_ID_REGEX, string));
			status.setBoardName(getMatchedString(TODAY_BOARD_NAME_REGEX, string));
			status.setPostNumberToday(Integer.parseInt(getMatchedString(
					TODAY_BOARD_TOPIC_NUM_REGEX, string)));
			status.setTotalPostToday(postNum);
			status.setRating(i + 1);
			list.add(status);
		}
		return list;
	}

    public String parseMsgContent(String html) {
        Pattern p = Pattern.compile("(?<=<div id=\"ubbcode1\" >).*?(?=</div>)");
        Matcher m = p.matcher(html);
        if (!m.find()) {
            throw new IllegalStateException("can not get msg content");
        }
        return m.group();
    }

    public String parseUploadPicture(String html) throws ParseContentException {
        return RegexUtil.getMatchedString(
                CC98ParseRepository.UPLOAD_PIC_ADDRESS_REGEX, html)
                .replace(",1", "");
    }
}
