from fastapi import HTTPException, status


class InvalidCredentials(HTTPException):
    def __init__(self):
        super().__init__(status_code=status.HTTP_401_UNAUTHORIZED, detail="잘못된 PIN입니다")


class TokenExpired(HTTPException):
    def __init__(self):
        super().__init__(status_code=status.HTTP_401_UNAUTHORIZED, detail="토큰이 만료되었습니다")


class Forbidden(HTTPException):
    def __init__(self, detail: str = "권한이 없습니다"):
        super().__init__(status_code=status.HTTP_403_FORBIDDEN, detail=detail)


class NotFound(HTTPException):
    def __init__(self, detail: str = "리소스를 찾을 수 없습니다"):
        super().__init__(status_code=status.HTTP_404_NOT_FOUND, detail=detail)


class BadRequest(HTTPException):
    def __init__(self, detail: str = "잘못된 요청입니다"):
        super().__init__(status_code=status.HTTP_400_BAD_REQUEST, detail=detail)
