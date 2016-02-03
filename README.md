## Why is it possible to intercept the management traffic?
- The traffic goes over broadcast.
- The traffic is encrypted using RC4 with a fixed key located *hardcoded* in the "Easy Smart Configuration Utility"

## Is it possible to configure foreign devices?
Yes, after intercepting the password (default is admin:admin on all devices, the maximum password length is 16) without any restrictions.

##Package
`<header>[<type><length><value>]`  
The body may contain more than one data block called "TLV".

##General communications
The password is transmitted on each configuration change request.

##List of possibly affected devices
- TL-SG1024DE 1.0
- TL-SG1024DE 2.0
- TL-SG1016DE 1.0
- TL-SG1016DE 2.0
- TL-SG108E 1.0
- TL-SG108E 2.0
- TL-SG105E 1.0
- TL-SG105E 2.0
- TL-SG108PE 1.0

##Sample output for a login

	===NEW PACKET ON PORT 29808 FROM /192.168.0.10
	Decrypted data:
	 Version:1
	 OPCODE:3
	 MAC:F4F26DA4F8DE
	 HOST MAC:08002738F48B
	 SEQUENCE NUMBER:102
	 ERROR CODE (7 or 8 are somehow bad):0
	 LENGTH:67
	 FRAGMENT OFFSET:0
	 TOKEN ID:22238
	 CHECKSUM (apparently not implemented):0
	  BODY:
	 -START TLV-
	  TYPE:512 (Username)
	  LENGTH:6
	  BODY:61646D696E00 (String: admin)
	 -START TLV-
	  TYPE:514 (Password)
	  LENGTH:17
	  BODY:6161616161616161616161616161616100 (String: aaaaaaaaaaaaaaaa)
	 -START TLV-
	  TYPE:-1 (null)
	  LENGTH:0
	  BODY: (String: )
	  RAW: 0103F4F26DA4F8DE08002738F48B00660000000000430000000056DE000000000200000661646D696E00020200116161616161616161616161616161616100FFFF0000

