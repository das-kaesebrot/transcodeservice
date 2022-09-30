import subprocess
import shutil

class FfmpegInfo:
    
    _info: dict = None
    _codecs: list[dict] = None
    _muxers: list[dict] = None
    _supported_video_encoders: list[str] = None
    _supported_audio_encoders: list[str] = None
    _supported_formats: list[str] = None
    _supported_pix_fmts: list[str] = None
    
    def __init__(self) -> None:
        pass
    
    def get_cached_info(self):
        if not self._info:
            self._info = FfmpegInfo.get_info()
            
        return self._info
    
    def get_cached_codecs(self):
        if not self._codecs:
            self._codecs = FfmpegInfo.get_ffmpeg_codecs()
            
        return self._codecs
    
    def get_cached_muxers(self):
        if not self._muxers:
            self._muxers = FfmpegInfo.get_ffmpeg_muxers()
            
        return self._muxers
    
    def get_cached_supported_video_encoders(self) -> list[str]:        
        if not self._supported_video_encoders:
            self._supported_video_encoders = FfmpegInfo.get_supported_video_encoders(self.get_cached_codecs())
            
        return self._supported_video_encoders
    
    def get_cached_supported_audio_encoders(self) -> list[str]:        
        if not self._supported_audio_encoders:
            self._supported_audio_encoders = FfmpegInfo.get_supported_audio_encoders(self.get_cached_codecs())
            
        return self._supported_audio_encoders

    def get_cached_supported_formats(self) -> list[str]:        
        if not self._supported_formats:
            self._supported_formats = FfmpegInfo.get_supported_formats(self.get_cached_muxers())
            
        return self._supported_formats
    
    @staticmethod
    def get_supported_video_encoders(codecs: list[dict] = None) -> list[str]:
        if not codecs:
            codecs = FfmpegInfo.get_ffmpeg_codecs()
            
        encoders = []        
        for entry in codecs:
            _ = entry.get("capabilities")
            if _.get("encoding_supported") and (_.get("codec_type") == "video"):
                encoders.append(entry.get("name"))        
        return encoders
    
    @staticmethod
    def get_supported_audio_encoders(codecs: list[dict] = None) -> list[str]:
        if not codecs:
            codecs = FfmpegInfo.get_ffmpeg_codecs()
            
        encoders = []        
        for entry in codecs:
            _ = entry.get("capabilities")
            if _.get("encoding_supported") and (_.get("codec_type") == "audio"):
                encoders.append(entry.get("name"))        
        return encoders
    
    @staticmethod
    def get_supported_formats(muxers: list[dict] = None) -> list[str]:
        if not muxers:
            muxers = FfmpegInfo.get_ffmpeg_muxers()
            
        formats = []        
        for entry in muxers:
            formats.append(entry.get("name"))
        
        return formats
    
    @staticmethod
    def get_info():
        return {
            "ffmpeg": {
                "binpath": FfmpegInfo.get_ffmpeg_binpath(), 
                "version": FfmpegInfo.get_ffmpeg_version(),
                "capabilities": FfmpegInfo.get_ffmpeg_capabilities()
            },
            "ffprobe": {
                "binpath": FfmpegInfo.get_ffprobe_binpath(),
                "version": FfmpegInfo.get_ffprobe_version()
            }
        }
    
    @staticmethod
    def get_ffmpeg_capabilities():
        return {
            "codecs": FfmpegInfo.get_ffmpeg_codecs(),
            "muxers": FfmpegInfo.get_ffmpeg_muxers(),
            "demuxers": FfmpegInfo.get_ffmpeg_demuxers()
        }
        
    @staticmethod
    def get_ffmpeg_version():
        result = subprocess.run("ffmpeg -v quiet -version".split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        return result.stdout.decode('utf8').split('\n')[0]
    
    @staticmethod
    def get_ffprobe_version():
        result = subprocess.run("ffprobe -v quiet -version".split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        return result.stdout.decode('utf8').split('\n')[0]
    
    @staticmethod
    def get_ffmpeg_binpath() -> str | None:
        return shutil.which("ffmpeg")
    
    @staticmethod    
    def get_ffprobe_binpath() -> str | None:
        return shutil.which("ffprobe")
    
    @staticmethod
    def get_ffmpeg_codecs() -> list[dict]:
        result = subprocess.run("ffmpeg -v quiet -codecs".split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        data = result.stdout.decode('utf8').split('\n')[12:]
        return FfmpegInfo.parse_codec_rows_as_dict(data, cut_columns=0)
    
    @staticmethod
    def get_ffmpeg_muxers() -> list[dict]:
        result = subprocess.run("ffmpeg -v quiet -muxers".split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        data = result.stdout.decode('utf8').split('\n')[4:]
        return FfmpegInfo.parse_rows_as_dict(data)
    
    @staticmethod
    def get_ffmpeg_demuxers() -> list[dict]:
        result = subprocess.run("ffmpeg -v quiet -demuxers".split(), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        data = result.stdout.decode('utf8').split('\n')[4:]
        return FfmpegInfo.parse_rows_as_dict(data)
    
    
    @staticmethod
    def parse_codec_rows_as_dict(data: list, cut_columns: int = 0, desc_from: int = 2) -> list[dict]:
        result_list = []
        
        for row in data:
            parsed_row = row.lstrip().split()
            if len(parsed_row) > cut_columns:
                parsed_row = parsed_row[cut_columns:]
                result_list.append({
                    "name": parsed_row[1],
                    "capabilities": FfmpegInfo.parse_codec_info(parsed_row[0]),
                    "description": ' '.join(parsed_row[desc_from - cut_columns:]),
                    "args": parsed_row[0]
                })
            
        return result_list
    
    @staticmethod
    def parse_rows_as_dict(data: list, cut_columns: int = 1, desc_from: int = 2) -> list[dict]:
        result_list = []
        
        for row in data:
            parsed_row = row.lstrip().split()
            if len(parsed_row) > cut_columns:
                parsed_row = parsed_row[cut_columns:]
                result_list.append({
                    "name": parsed_row[0],
                    "description": ' '.join(parsed_row[desc_from - cut_columns:])
                })
            
        return result_list
    
    @staticmethod
    def parse_codec_info(args: str) -> dict:
        """
        Codecs:
        D..... = Decoding supported
        .E.... = Encoding supported
        ..V... = Video codec
        ..A... = Audio codec
        ..S... = Subtitle codec
        ..D... = Data codec
        ..T... = Attachment codec
        ...I.. = Intra frame-only codec
        ....L. = Lossy compression
        .....S = Lossless compression
        """
        
        if len(args) != 6:
            return
        
        decoding_supported = False
        encoding_supported = False
        codec_type = None
        iframe_only = False
        lossy = False
        lossless = False
        
        if args[0] == "D":
            decoding_supported = True
        
        
        if args[1] == "E":
            encoding_supported = True
            
        
        if args[2] == "V":
            codec_type = "video"
        
        elif args[2] == "A":
            codec_type = "audio"
        
        elif args[2] == "S":
            codec_type = "subtitle"
        
        elif args[2] == "D":
            codec_type = "data"
            
        elif args[2] == "T":
            codec_type = "attachment"
            
        
        if args[3] == "I":
            iframe_only = True
            
            
        if args[4] == "L":
            lossy = True
            
        if args[5] == "S":
            lossless = True
        
        return {
            "decoding_supported": decoding_supported,
            "encoding_supported": encoding_supported,
            "codec_type": codec_type,
            "intraframe_only": iframe_only,
            "lossy_compression": lossy,
            "lossless_compression": lossless
        }