# main.py
import os
import uuid
from fastapi import FastAPI, UploadFile, File
from fastapi.responses import JSONResponse

from document_api import ingest_to_db, API_KEY
from parsing import parse_file_to_json

app = FastAPI()


# =========================
# 기업마당 공고 수집
# =========================
@app.post("/collect/notices")
def collect_notices():
    """
    기업마당 기술공고 수집
    - document_api.ingest_to_db() 호출
    - project_notices, notice_files, notice_hashtags 테이블에 저장
    """
    print("🔥 COLLECT CALLED")
    inserted = ingest_to_db(API_KEY)
    print(f"🔥 COLLECT DONE: {inserted}건 수집")
    return {"inserted": inserted}


# =========================
# 파일 파싱 (DB 저장은 Spring에서)
# =========================
@app.post("/parse")
async def parse_notice(file: UploadFile = File(...)):
    """
    파일 파싱만 수행 (DB 저장은 Spring Boot에서 처리)
    
    Flow:
    1. Spring Boot: NoticeFile 생성 + NoticeAttachment 생성 (WAIT 상태)
    2. Spring Boot → FastAPI: 파일 전송
    3. FastAPI: 파싱 수행 후 결과 JSON 반환 ← 이 함수
    4. Spring Boot: NoticeAttachment.markDone(parsedJson) 호출
    """
    print(f"🔥 PARSE CALLED: {file.filename}")

    os.makedirs("tmp", exist_ok=True)
    ext = os.path.splitext(file.filename)[1].lower()
    tmp_path = os.path.join("tmp", f"{uuid.uuid4().hex}{ext}")

    try:
        # 1️⃣ 파일 임시 저장
        content = await file.read()
        with open(tmp_path, "wb") as f:
            f.write(content)

        # 2️⃣ 파싱
        parsed = parse_file_to_json(tmp_path)

        print(f"✅ PARSE SUCCESS: {file.filename}")

        # 3️⃣ 파싱 결과만 반환 (DB 저장은 Spring에서)
        return JSONResponse(
            content=parsed,
            status_code=200
        )

    except Exception as e:
        print(f"❌ PARSE FAILED: {file.filename} - {str(e)}")
        
        return JSONResponse(
            status_code=500,
            content={"error": str(e)}
        )

    finally:
        # 임시 파일 삭제
        if os.path.exists(tmp_path):
            os.remove(tmp_path)


# =========================
# 헬스체크
# =========================
@app.get("/health")
def health_check():
    return {"status": "ok", "message": "FastAPI is running"}


# =========================
# 파싱 상태 조회 (선택사항)
# =========================
@app.get("/parse/formats")
def supported_formats():
    """
    지원하는 파일 형식 조회
    """
    return {
        "supported_formats": [".pdf", ".docx"],
        "max_file_size_mb": 50
    }