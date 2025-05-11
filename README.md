1. Texturen werden aus dem Ordner geladen
2. Texturen werden im SpriteAtlasHolder (PaintingManager) zusammen gestitched
3. SpriteAtlas wird im Texturemanager als Textur registriert
4. Beim Entityrenderer nimmt er die PaintingVariant, welche die Größen und Koordinaten im Atlas beeinhaltet
    - Hier wird dann die Texture vom Atlas über den Identifier geladen und gerendert

Was wir machen:
- Texturen aus dem Internet laden und PaintingVariants erstellen
- SpriteAtlas insgesamt manuell reloaden und Texturen da injecten (alternativ den Reload-Prozess überschreiben)
- Texturen werden im TextureManager (hopefully) registriert
