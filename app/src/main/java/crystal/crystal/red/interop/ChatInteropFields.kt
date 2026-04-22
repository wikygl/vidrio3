package crystal.crystal.red.interop

object ChatInteropFields {

    object Chat {
        const val ID = "id"
        const val NAME = "name"
        const val USERS = "users"
        const val PARTICIPANTS_KEY = "participantsKey"
        const val LAST_MSG_DATE = "lastMsgDate"
        const val LAST_MESSAGE_PREVIEW = "lastMessagePreview"
        const val LAST_MESSAGE_TYPE = "lastMessageType"
        const val PEER_PLATFORM = "peerPlatform"
        const val PEER_EXTERNAL_USER_ID = "peerExternalUserId"
        const val PEER_COMPANY_NAME = "peerCompanyName"
        const val PEER_CAN_RECEIVE = "peerCanReceive"
        const val UNREAD_BY = "unreadBy"
    }

    object Message {
        const val ID = "id"
        const val MESSAGE = "message"
        const val FROM = "from"
        const val FROM_UID = "fromUid"
        const val TYPE = "type"
        const val LEGACY_TYPE = "tipo"
        const val FILE_URL = "fileUrl"
        const val FILE_NAME = "fileName"
        const val LEGACY_FILE_NAME = "nombreArchivo"
        const val SOURCE_APP = "sourceApp"
        const val TARGET_APP = "targetApp"
        const val TARGET_EXTERNAL_USER_ID = "targetExternalUserId"
        const val SYNC_STATUS = "syncStatus"
        const val SCHEMA_VERSION = "schemaVersion"
        const val PAYLOAD = "payload"
        const val CREATED_AT = "dob"
    }
}
